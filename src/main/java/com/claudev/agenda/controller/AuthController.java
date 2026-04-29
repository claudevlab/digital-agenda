package com.claudev.agenda.controller;

import com.claudev.agenda.dto.*;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.Role;
import com.claudev.agenda.mapper.UserMapper;
import com.claudev.agenda.security.JwtUtil;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final UserService userService;
    private final UserMapper userMapper;

    // nuove dipendenze per il login
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService , UserMapper userMapper ,AuthenticationManager authenticationManager , JwtUtil jwtUtil) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
    public ResponseEntity<AuthResponseDTO> login (@Valid @RequestBody UserLoginDTO userLoginDTO) {

        // deleghiamo a spring security il controllo password
        // se la password non e' corretta lancera' un eccezione (BadCredentialException)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(),userLoginDTO.getPassword())
        );

        // Recupera l'utente dal DB per avere tutti i dati
        User user = userService.getUserByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // arrivato a questo punto vuol dire che le credenziali sono corrette
        // Dunque generiamo il token
        String token = jwtUtil.generateToken(userLoginDTO.getEmail());

        // Restituisce il DTO completo (non solo il token!)
        AuthResponseDTO response = new AuthResponseDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getPhoneNumber()// converte l'enum in stringa
        );

        return ResponseEntity.ok(response);
    }

    // endpoitn recupero password 1
    // il frontend invia solo le email
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword (
            @Valid @RequestBody
            ForgotPasswordRequestDTO requestDTO) {
                try {
                    userService.generatePasswordResetToken(requestDTO.getEmail());
                    // Rispondiamo sempre con 200 OK anche se l'email non esiste, per motivi di sicurezza
                    // (evita attacchi di "User Enumeration") ma nel nostro Service lanciamo un'eccezione
                    // ai puo' gestire con un @ExceptionHandler globale.
                    // Per ora mandiamo un semplice messaggio di successo.
                    return ResponseEntity.ok("Se l'email esiste nel sistema, riceverai un link per reimpostare la password.");
                } catch (RuntimeException e) {
                    return  ResponseEntity.badRequest().body(e.getMessage());
    }

}

// Endpoint 2: Il frontend invia il token estratto dall'URL e la nuova password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword (@Valid @RequestBody
                                            ResetPasswordRequestDTO requestDTO) {
        try {
            userService.resetPassword(requestDTO.getToken(),requestDTO.getNewPassword());
            return ResponseEntity.ok("Password aggiornata con successo. Ora puoi accedere.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
