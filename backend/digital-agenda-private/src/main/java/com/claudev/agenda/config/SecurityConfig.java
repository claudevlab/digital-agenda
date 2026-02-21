package com.claudev.agenda.config;

import com.claudev.agenda.security.CustomUserDetailsService;
import com.claudev.agenda.security.JwtAuthenticationFilter;
import com.claudev.agenda.security.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // per configurare spring security
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final AppConfig appConfig;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          AppConfig appConfig) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.appConfig = appConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http)  {
        http.
                csrf(AbstractHttpConfigurer::disable) // disabilito il csrf ( standard per API REST stateless
                .authorizeHttpRequests(auth -> auth

                        // Auth (login/register) aperto a tutti
                        .requestMatchers("/api/auth/**").permitAll() // autorizza gli url pubblici /login/register

                        //Swagger aperto a tutti per la documentazione
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        /*
                        // api schedules richiede autentificazione JWT
                        .requestMatchers("/api/schedules/**").authenticated()

                         */

                        .anyRequest().authenticated()  // tutto il resto richiede il token
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // niente sessioni server
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
