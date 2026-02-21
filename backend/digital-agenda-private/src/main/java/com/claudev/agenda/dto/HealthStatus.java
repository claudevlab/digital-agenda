package com.claudev.agenda.dto;

/*
 rappresenta una sorta di stato di salute dell'applicazione

 ho utilizzato un record per essere piu' moderno e scrivere meno codice
 */
public record HealthStatus(
        String status,
        long uptime,
        String timestamp,
        String message) {

}
