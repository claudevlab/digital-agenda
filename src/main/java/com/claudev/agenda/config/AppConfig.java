package com.claudev.agenda.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

/*
per evitare l'errore della circular dependency
 */
@Configuration
public class AppConfig {

    // password criptata
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    // mappa automaticamente i JSON in oggetti java
    // utile per le API esterne e microservices
    // utile per il microservices delle email notification
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
