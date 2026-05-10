package com.claudev.agenda.config;

import com.claudev.agenda.security.CustomUserDetailsService;
import com.claudev.agenda.security.JwtAuthenticationFilter;
import com.claudev.agenda.security.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // per configurare spring security
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final AppConfig appConfig;

    @Value("$(app.allowed-origins:http:localhost:4200}")
    private String[] allowedOrigins;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          AppConfig appConfig) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.appConfig = appConfig;
    }

    // CORS CONFIG piu permissivo

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Questo è il trick: accettare il pattern universale invece di origini fisse
        // per bypassare il controllo rigido dell'Origin header inviato da Traefik
        config.setAllowedOriginPatterns(List.of("*"));

        // Assicurati che tutti i metodi siano consentiti (soprattutto OPTIONS)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));

        // Permetti tutti gli header, in particolare l'Authorization (usato per il login)
        config.setAllowedHeaders(Arrays.asList("*"));

        // Esponi gli header per farli leggere ad Angular
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));

        // Credenziali a true è fondamentale se il front-end manda cookie/token di sessione
        config.setAllowCredentials(true);

        // Aumenta il tempo di validità del preflight così Angular non lo richiede ogni volta
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applica a tutti i path ("/**" e non solo "/api/**" per sicurezza massima)
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    /*
    // PRODUZIONE : FIX  BUG cors preflight
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Consentiamo le origini ma per sicurezza aggiungiamo i domini
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // Autorizziamo tutti i metodi necessari, IMPORTANTISSIMO IL METODO 'OPTIONS'
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true); // Serve se passi i token nei cookie o localStorage

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

     */

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable) // disabilito il csrf , utilizziamo JWT
                //.cors(cors -> cors.configurationSource(corsConfigurationSource())) // utilizziamo il bean creato sopra

                .authorizeHttpRequests(auth -> auth

                        // FIX BUG cors preflight : lasciamo passare tutte le richieste OPTIONS per evitare blocchi CORS(401)
                        /* fix 2 --> il frontend manda una richiesta preliminare che e' priva di token
                        e spring security la reindirizzava a /login
                        con permitAll() invece lo consente
                         */
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth (login/register) aperto a tutti
                        .requestMatchers("/api/auth/**").permitAll() // autorizza gli url pubblici /login/register

                        //Swagger aperto a tutti per la documentazione
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Per evitare grattacapi futuri inseriamo un inoltro automatico alla rotta nascosta /error
                        // rende piu' facile il debug in futuro
                        .requestMatchers("/error").permitAll()

                        /*
                        // api schedules richiede autentificazione JWT
                        .requestMatchers("/api/schedules/**").authenticated()

                         */

                        .anyRequest().authenticated()  // tutto il resto richiede il token
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // niente sessioni server
                )
                // se la richiesta non e' autenticata rispondi 401 (evita il redirect a google)
                .exceptionHandling( ex -> ex
                        .authenticationEntryPoint(((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Non autorizzato. Token mancante o scaduto.\"}");
                        }))

                        )
                .authenticationProvider(authenticationProvider()) // provider custom
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // aggiungo filtro Jwt
                .oauth2Login(oauth2 -> oauth2
                        // Quando il login ha successo, Spring chiamerà questo handler
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        return http.build();
    }

    // bean che dice a SPring come verificare le credenziali
    @Bean
    public AuthenticationProvider authenticationProvider () {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(customUserDetailsService); // utilizza il mio custoUserService per caricare gli utenti
        daoAuthenticationProvider.setPasswordEncoder(appConfig.passwordEncoder()); // usa il Bcrypt per le password
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
