package com.claudev.agenda.dto;


import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ScheduleExceptionRequestDTO {

    @NotNull(message = "Inserisci una data")
    private LocalDate date;

    private String reason;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
