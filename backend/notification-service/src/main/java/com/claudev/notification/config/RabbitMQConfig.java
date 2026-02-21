package com.claudev.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //per il primo avvio diciamo al microservizio che se non esiste la coda (non esiste) la crea
    // altrimenti va in errore e non parte
    @Bean
    public Queue queue() {
        return new Queue("notification-queue",true);
    }

   @Bean
   public MessageConverter jsonMessageConverter () {
       return new JacksonJsonMessageConverter();
   }
}
