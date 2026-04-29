package com.claudev.agenda.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
"OncePerRequestFilter" --> fa' il controllo una sola volta
es. buttafuori che ti da' il bracciale per poter entrare uscire dal locale per tutta la durta dell'evento ,
senza che ti chiede ogni volta il biglietto
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        logger.info("FILTER CHECK - Header: {}", authHeader); // <--- LOG 1
        final String jwt;
        final String userEmail;

        // 1 - controllo preliminare : se manca l'header oppire non e' Bearer passa oltre cioe' lascia gestire a spring security
        if (authHeader == null  || !authHeader.startsWith("Bearer ")) {  // l'intestazione inizia con bearer + spazio

            logger.info("Nessun token o formato errato. Passo oltre."); // <--- LOG 2
            filterChain.doFilter(request,response);
            return;
        }

        // 2 - Estrazione del token
        jwt = authHeader.substring(7); // esclusa la parola Bearer + spazio

        // 3 - estrae l'email dal token
        try {
            userEmail = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            // se il token e danneggiato o scaduto il codice continua rendendo visibili le pagine "pubbliche"
            // poi si occupera' il try/catch di assegnare l'utente come anonimo e non null
            logger.info(" Errore nel filtro JWT: {}", e.getMessage()); // <--- LOG ERRORE
            filterChain.doFilter(request, response);
            return;  // fine lavoro --> non esegue altri controlli non piu' necessari
        }

        // 4 - se abbiamo l'emai ma l'utente non risulta loggato
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // carica l'utente nel DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5 - se il toke e' valido dai l'accesso
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails ,
                        null,
                        userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // salva l'autentificazione nel contesto di spring
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // passa al prossimo giltro
        filterChain.doFilter(request,response);


    }

    // Questo metodo evita che il filtro scatta per le rotte di login/registrazione
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }
}
