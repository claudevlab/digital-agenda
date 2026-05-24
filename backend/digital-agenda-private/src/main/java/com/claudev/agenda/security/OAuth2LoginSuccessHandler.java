package com.claudev.agenda.security;

import com.claudev.agenda.entity.User;
import com.claudev.agenda.repository.UserRepository;
import com.claudev.agenda.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;



import java.io.IOException;

// classe di spring che definisce le azioni da compiere dopo che un utente si e' autentificato con successo
// Simple.. gestisce la navigazione post login
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository,UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void onAuthenticationSuccess (HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication )
        throws IOException , ServletException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // estraggo i dati da google
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        // utilizziamo mapStruct
        User user = userService.processOAuthPostLogin(email,firstName,lastName);


        /*
        // cerchiamo l'utente nel DB o lo crea
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setRole(Role.CUSTOMER);
            newUser.setPassword(""); // vuoto perche' accede via google
            return userRepository.save(newUser);
        });

  */

         // genera token jwt
        String token = jwtUtil.generateToken(user.getEmail() , user.getRole().name());

        //reindirizzamento al frontend con il token nell'URL
        // in produzione si utilizzano i cookie o un redirect sicuro , per ora lascio cosi ---TO DO
        response.sendRedirect(frontendUrl + "/login-success?token=" + token);

    }


}
