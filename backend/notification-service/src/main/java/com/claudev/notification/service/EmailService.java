package com.claudev.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail (String to, String subject , String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@agenda-digitale.com"); // mittente finto
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println("Ho inviato una mail a : " + to); // log per il debug
    }
}
