package com.claudev.notification.service;

import com.claudev.notification.dto.EmailRequestDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final EmailService emailService;

    public NotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "notification-queue") // coincide con il nome dell'app monolitica
    public void  consumeMessagge(EmailRequestDTO emailRequest) {
        System.out.println("Messaggio ricevuto da RabbitMQ monolitica");

        // la mail la invia per davvero
        emailService.sendEmail(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody()
        );
    }
}
