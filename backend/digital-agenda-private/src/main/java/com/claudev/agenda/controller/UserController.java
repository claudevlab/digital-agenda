package com.claudev.agenda.controller;

import com.claudev.agenda.dto.UpdatePhoneDTO;
import com.claudev.agenda.dto.UpgradeProfessionalDTO;
import com.claudev.agenda.dto.UserProfessionalResponseDTO;
import com.claudev.agenda.dto.UserResponseDTO;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.mapper.UserMapper;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // GET /api/users/professionals
    // Restituisce tutti gli utenti con ruolo PROFESSIONAL
    @GetMapping("/professional")
    public ResponseEntity<List<UserProfessionalResponseDTO>> getProfessionals () {
        return  ResponseEntity.ok(userService.getAllProfessionals());
    }

    @GetMapping("/professionals")
    public ResponseEntity<List<UserProfessionalResponseDTO>> getProfessionals (@RequestParam (required = false) String search ) {
    return ResponseEntity.ok(userService.getProfessionals(search));
    }

    @PatchMapping("/upgrade-to-professional")
    public ResponseEntity<?> upgradeUser (@Valid @RequestBody UpgradeProfessionalDTO upgradeProfessionalDTO) {
        try {
            // recupera email dal token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserMail = authentication.getName();

            // aggiornamento utente
            User upgradeUser = userService.upgradeToProfessional(currentUserMail, upgradeProfessionalDTO);

            return ResponseEntity.ok("Congratulazioni, sei diventato un Professionista! Effettua nuovamente il login per accedere alla tua nuova Dashboard.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        }


    @PatchMapping("/update-phone")
    public ResponseEntity<?> updatePhone(@Valid @RequestBody UpdatePhoneDTO dto) {
        // Prende l'email dal token JWT dell'utente loggato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.updatePhoneNumber(authentication.getName(), dto.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        // recupera l'email dal token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // recupero utente dal DB
        User user = userService.getUserByEmail(email).orElseThrow(() -> new RuntimeException());

        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setPhoneNumber(user.getPhoneNumber());

        response.setJobTitle(user.getJobTitle());
        response.setRemote(user.isRemote());
        response.setOnSite(user.isOnSite());


        return ResponseEntity.ok(response);


    }

    @PatchMapping("/me/professional-profile")
    public ResponseEntity<?> updateProfile (
            @Valid @RequestBody UpgradeProfessionalDTO upgradeProfessionalDTO
    ) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.upgradeProfessionalDTO(email, upgradeProfessionalDTO);

            return ResponseEntity.ok("Profilo aggiornato con successo");
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }

    }


    }


