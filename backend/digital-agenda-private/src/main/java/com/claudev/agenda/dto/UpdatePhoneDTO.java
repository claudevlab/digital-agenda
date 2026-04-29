package com.claudev.agenda.dto;

import jakarta.validation.constraints.NotBlank;

/*
serve per il login con google
in secondo momento gli chiederemo di inserire obbligatoriamente il numero di telefono utile per il business
 */
public class UpdatePhoneDTO {

    @NotBlank(message = "il numero di telefono e' obbligatorio")
    private  String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
