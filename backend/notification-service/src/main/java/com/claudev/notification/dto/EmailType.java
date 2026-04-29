package com.claudev.notification.dto;

public enum EmailType {

    NEW_BOOKING_TO_PROFESSIONAL,   // al professionista: nuova prenotazione
    BOOKING_CONFIRMED_TO_CUSTOMER, // al cliente: confermato
    BOOKING_REJECTED_TO_CUSTOMER,   // al cliente: rifiutato/cancellato
    BOOKING_CANCELLED_TO_PROFESSIONAL, // al professionista : appuntamento
    REGISTRATION_CONFIRMED,
    RESET_PASSWORD_REQUEST,
    PASSWORD_CHANGED_SUCCESS
}

