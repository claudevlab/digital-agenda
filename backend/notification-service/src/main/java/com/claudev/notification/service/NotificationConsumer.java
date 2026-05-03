package com.claudev.notification.service;

import com.claudev.notification.dto.EmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final EmailService emailService;
    public  static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    public NotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "notification-queue") // coincide con il nome dell'app monolitica
    public void  consumeMessagge(EmailRequestDTO emailRequest) {
        logger.info("Messaggio ricevuto da RabbitMQ per: {}", emailRequest.getTo());
       emailService.sendHtmlEmail(emailRequest);
    }
}
