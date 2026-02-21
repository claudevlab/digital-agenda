package com.claudev.notification;

import com.claudev.notification.service.EmailService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

    /*
    // test pre - API
    @Bean
    CommandLineRunner run(EmailService emailService) {
        return args -> {
            System.out.println(" Test invio mail in corso...");
            emailService.sendEmail(
                    "cliente@example.com",
                    "Benvenuto nell'Agenda!",
                    "Ciao! Questa è una mail di prova dal tuo microservizio Spring Boot."
            );
        };
    }


     */
}
