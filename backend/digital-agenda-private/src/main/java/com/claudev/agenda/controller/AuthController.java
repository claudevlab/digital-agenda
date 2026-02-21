package com.claudev.agenda.controller;

import com.claudev.agenda.dto.*;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.Role;
import com.claudev.agenda.mapper.UserMapper;
import com.claudev.agenda.security.JwtUtil;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
        user.setRole(Role.CUSTOMER); // Oppure posso settarlo  nel mapper con @Mapping(constant="CUSTOMER")

        // salvataggio
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserReponse(savedUser));
    }

    // professional registration /api/auth/register/professional
    @PostMapping("/register/professional")
    public ResponseEntity<?> registerProfessional (@Valid @RequestBody UserProfessionalRegistrationDTO userProfessionalRegistrationDTO) {

        // utilizzo il Mapper : DTO -> entity
        User user = userMapper.toEntityProfessional(userProfessionalRegistrationDTO);
        // il ruolo professional verra' settato nel mapper

        // salvataggio
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserReponse(savedUser));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login (@Valid @RequestBody UserLoginDTO userLoginDTO) {

        // deleghiamo a spring security il controllo password
        // se la password non e' correttta lancera' un eccezzione (BadCredentialException)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(),userLoginDTO.getPassword())
        );

        // arrivato a questo punto vuol dire che le credenziali sono corrette
        // Dunque generiamo il token
        String token = jwtUtil.generateToken(userLoginDTO.getEmail());

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
