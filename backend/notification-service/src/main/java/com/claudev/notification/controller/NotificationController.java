package com.claudev.notification.controller;

import com.claudev.notification.dto.EmailRequestDTO;
import com.claudev.notification.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public String sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        emailService.sendHtmlEmail(emailRequestDTO); //  nuovo metodo
        return "Email inviata con successo a: " + emailRequestDTO.getTo();
    }
}
