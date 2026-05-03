package com.claudev.notification.service;

import com.claudev.notification.dto.EmailType;
import io.github.bucket4j.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.claudev.notification.dto.EmailRequestDTO;

import java.time.Duration;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    public static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /*
    === CRITICITA' ===

    Mettiamo in caso che per qualsiasi bug il sistema all'impazzata manda 100.10000 mail la queue di rabbit esplode
    inseriamo quindi un limit rate

     */

    private final Bucket emailBucket;


    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;

        // impostiamo un limite di max 100 mail al secondo
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofSeconds(1)));
        this.emailBucket = Bucket4j.builder().addLimit(limit).build();
    }

    public void sendHtmlEmail(EmailRequestDTO requestDto) {

        try {

            if (!emailBucket.tryConsume(1)) {
                // Queue message di nuovo dopo 1 secondo
                logger.warn("Rate limit raggiunto, rinviando email in coda");
                // Re-publish al rabbitmq o logica di retry
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("notifications@digital-agenda.it");
            helper.setTo(requestDto.getTo());
            helper.setSubject(requestDto.getSubject());

            // prepara le variabili per il template
            Context context = new Context();
            context.setVariable("professionalName", requestDto.getProfessionalName());
            context.setVariable("customerName", requestDto.getCustomerName());
            context.setVariable("appointmentDateTime", requestDto.getAppointmentDateTime());
            context.setVariable("durationMinutes", requestDto.getDurationMinutes());
            context.setVariable("reasonRejected", requestDto.getReasonRejected());
            context.setVariable("appUrl", requestDto.getAppUrl());

            // scelta del template in base al tipo di email
            String templateName = resolveTemplate(requestDto.getEmailType());
            String htmlBody = templateEngine.process(templateName, context);

            helper.setText(htmlBody, true); // true = isHtml
            mailSender.send(message);

            logger.info("Email HTML inviata a: {} con template: {}", requestDto.getTo(), templateName);

        } catch (MessagingException e) {
            throw new RuntimeException("Errore invio email HTML", e);
        }
    }

    private String resolveTemplate(EmailType emailType) {
        return switch (emailType) {
            case NEW_BOOKING_TO_PROFESSIONAL -> "new-booking";
            case BOOKING_CONFIRMED_TO_CUSTOMER -> "booking-confirmed";
            case BOOKING_REJECTED_TO_CUSTOMER -> "booking-rejected";
            case BOOKING_CANCELLED_TO_PROFESSIONAL -> "booking-cancelled-professional";
            case REGISTRATION_CONFIRMED -> "registration-confirmed";
            case RESET_PASSWORD_REQUEST -> "reset-password";
            case PASSWORD_CHANGED_SUCCESS -> "password-changed-success";
        };
    }

}


/* VECCHIO METODO solo scritta
    public void sendEmail (String to, String subject , String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@agenda-digitale.com"); // mittente finto
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        logger.info("Ho inviato una mail a : {}" , to); // log per il debug
    }

 */

