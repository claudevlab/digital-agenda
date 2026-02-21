package com.claudev.agenda.security;

import com.claudev.agenda.entity.User;
import com.claudev.agenda.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// risolve il problema di come caricare gli utenti dal DB in modo custom

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // serve a dire a spring come deve caricare un utente nel DB

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername (String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email" + email));

        // costruzione dell'oggetto UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())   // diventa ruolo_professional
                .build();
    }
}
