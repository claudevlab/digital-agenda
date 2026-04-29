package com.claudev.agenda.controller;

import com.claudev.agenda.dto.HealthStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("api/health")
public class HealthController {

    @GetMapping
    public HealthStatus getHealth () {
        return new HealthStatus("Up",System.currentTimeMillis(), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),"applicazione avviata con successo");
    }
}
