package com.claudev.agenda.dto;

public class AuthResponseDTO {

    // e sempre buona norma restituire un oggetto JSON strutturato per evitare una stringa nuda e cruda

    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
