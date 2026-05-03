package com.claudev.notification.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// --- metrica Prometheus per osservare il backlog della coda

@Component
public class RabbitMQMetrics {

    private final RabbitTemplate rabbitTemplate;
    private final MeterRegistry meterRegistry;

    public RabbitMQMetrics(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedRate = 5000) // Ogni 5 secondi
    public void publishQueueDepth() {
        RabbitAdmin admin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
        QueueInformation info = admin.getQueueInfo("email-queue");

        if (info != null) {
            meterRegistry.gauge("rabbitmq.queue.depth", info.getMessageCount());
        }
    }
}
