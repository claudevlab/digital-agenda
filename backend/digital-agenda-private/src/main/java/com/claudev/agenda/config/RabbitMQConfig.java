package com.claudev.agenda.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "notification-queue";
    public static final String EXCHANGE_NAME = "notification-exchange";
    public static final String ROUTING_KEY = "notification.routing.key";

    // definiamo la coda (durevole = non si perde al ravvio di Rabbi)
   @Bean
    public Queue queue() {
       return new Queue(QUEUE_NAME,true);
   }

   // definiamo l'exchange (direct = routing esatto)
    @Bean
    public TopicExchange exchange () {
       return  new TopicExchange(EXCHANGE_NAME);
    }

    // leghiamo coda ed exchange con la routing key
    @Bean
    public Binding binding(Queue queue,TopicExchange exchange) {
       return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }


    // convertitore JSON --> gli mandiamo oggetti java anziche' i byte
    @Bean
    public MessageConverter jsonmessageConverter() {
       return  new JacksonJsonMessageConverter();
    }

    // template configurato con il server JSON
    public RabbitTemplate rabbitTemplate (ConnectionFactory connectionFactory) {
       RabbitTemplate template = new RabbitTemplate(connectionFactory);
       template.setMessageConverter(jsonmessageConverter());
       return template;
    }
}
