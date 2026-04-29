package com.claudev.agenda.client;

import com.claudev.agenda.config.RabbitMQConfig;
import com.claudev.agenda.dto.EmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;
    public  static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailNotification (EmailRequestDTO emailRequest) {

        // log per debug
        logger.info("Inviando un messaggio a RabbitMQ microservice all'indirizzo email: {} " , emailRequest);

        // per debug
        logger.info("Invio a RabbitMQ → to: {}, emailType: {}",
                emailRequest.getTo(), emailRequest.getEmailType());

        // invio asincrono
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,RabbitMQConfig.ROUTING_KEY,emailRequest);
    }
}
