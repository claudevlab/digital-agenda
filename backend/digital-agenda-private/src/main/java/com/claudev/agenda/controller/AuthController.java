package com.claudev.agenda.controller;

import com.claudev.agenda.dto.*;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.Role;
import com.claudev.agenda.mapper.UserMapper;
import com.claudev.agenda.security.JwtAuthenticationFilter;
import com.claudev.agenda.security.JwtUtil;
import com.claudev.agenda.security.TokenBlackListService;
import com.claudev.agenda.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final UserService userService;
    private final UserMapper userMapper;
    private  final TokenBlackListService tokenBlackListService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // nuove dipendenze per il login
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService ,
                          UserMapper userMapper ,
                          AuthenticationManager authenticationManager ,
                          JwtUtil jwtUtil,
                          TokenBlackListService tokenBlackListService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlackListService = tokenBlackListService;
    }

    // customer regisration /api/auth/register/customer
    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer (@Valid @RequestBody UsersRegistrationDTO usersRegistrationDTO) {
        // utilizzo il Mapper : DTO -> entity
        User user = userMapper.toEntityCustomer(usersRegistrationDTO);
        user.setRole(Role.CUSTOMER); // Oppure posso settarlo  nel mapper con @Mapping(constant="CUSTOMER") EDIT : meglio forzarlo per sicurezza

        // salvataggio
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserResponse(savedUser));
    }

    // professional registration /api/auth/register/professional
    @PostMapping("/register/professional")
    public ResponseEntity<?> registerProfessional (@Valid @RequestBody UserProfessionalRegistrationDTO userProfessionalRegistrationDTO) {

        // utilizzo il Mapper : DTO -> entity
        User user = userMapper.toEntityProfessional(userProfessionalRegistrationDTO);

        user.setRole(Role.PROFESSIONAL);

        // salvataggio
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserResponse(savedUser));
    }


    @PostMapping("/login")
    @RateLimiter(name = "login", fallbackMethod = "loginRateLimitFallback")
    public ResponseEntity<AuthResponseDTO> login (@Valid @RequestBody UserLoginDTO userLoginDTO) {

        // deleghiamo a spring security il controllo password
        // se la password non e' corretta lancerá un eccezione (BadCredentialException)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(),userLoginDTO.getPassword())
        );

        // Recupera l'utente dal DB per avere tutti i dati
        User user = userService.getUserByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // arrivato a questo punto vuol dire che le credenziali sono corrette
        // Dunque generiamo il token
        String token = jwtUtil.generateToken(userLoginDTO.getEmail(), user.getRole().name());

        // Restituisce il DTO completo (non solo il token!)
        AuthResponseDTO response = new AuthResponseDTO(
                token, // non serve piú il token
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getPhoneNumber()
        );

        return ResponseEntity.ok(response);
    }

    // Fallback quando rate limit exceeded
    public ResponseEntity<Map<String, String>> loginRateLimitFallback(
            UserLoginDTO userLoginDTO,
            Exception ex) {

        Map<String, String> errorResponse = Map.of(
                "error", "Troppi tentativi di login. Riprova tra 15 minuti"
        );

        return ResponseEntity.status(429)
                .header("Content-Type", "application/json")
                .body(errorResponse);
    }

    // endpoint recupero password 1
    // il frontend invia solo le email
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword (
            @Valid @RequestBody
            ForgotPasswordRequestDTO requestDTO) {

        long startTime = System.currentTimeMillis();

                try {
                    userService.generatePasswordResetToken(requestDTO.getEmail());
                } catch (RuntimeException e) {
                    // log interno
                    logger.warn("Tentativo di reset password di una mail inesistente");
    }

        // Forziamo sempre almeno 500ms di risposta per evitare timing attack
        long elapsed = System.currentTimeMillis() - startTime;
        long minDelay = 500L;
        if (elapsed < minDelay) {
            try {
                Thread.sleep(minDelay - elapsed);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        return  ResponseEntity.ok("Se l'email esiste riceverai un link per reimpostare la password");

}

// Endpoint 2: Il frontend invia il token estratto dall'URL e la nuova password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword (@Valid @RequestBody
                                            ResetPasswordRequestDTO requestDTO) {
            userService.resetPassword(requestDTO.getToken(),requestDTO.getNewPassword());
            return ResponseEntity.ok("Password aggiornata con successo. Ora puoi accedere.");
        }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request , HttpServletResponse httpServletResponse) {

        // Estrai token dall'header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Date expiration = jwtUtil.extractClaime(token, Claims::getExpiration);
            tokenBlackListService.blacklistToken(token, expiration);
        }

        return ResponseEntity.ok("Logout effettuato con successo.");
    }
    }


