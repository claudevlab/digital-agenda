package com.claudev.agenda.client;

import com.claudev.agenda.config.RabbitMQConfig;
import com.claudev.agenda.dto.EmailRequestDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailNotification (EmailRequestDTO emailRequest) {

        // log per debug
        System.out.println("Inviando un messaggio a RabbitMQ microservice " + emailRequest);

        // invio asincrono
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,RabbitMQConfig.ROUTING_KEY,emailRequest);
    }
}
