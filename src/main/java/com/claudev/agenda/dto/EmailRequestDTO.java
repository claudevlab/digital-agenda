package com.claudev.agenda.dto;

import com.claudev.agenda.enums.EmailType;

public class EmailRequestDTO {

    private String to;
    private String subject;
    private EmailType emailType;

    // dati dinamici per popolare il thymeleaf
    private String professionalName;
    private String customerName;
    private String appointmentDateTime;
    private Integer durationMinutes;
    private String notes;
    private String appUrl; // link alla web app
    private String reasonRejected;

    public EmailRequestDTO(String to,
                           String subject,
                           EmailType emailType,
                           String professionalName,
                           String customerName,
                           String appointmentDateTime,
                           Integer durationMinutes,
                           String notes,
                           String appUrl,
                           String reasonReject) {
        this.to = to;
        this.subject = subject;
        this.emailType = emailType;
        this.professionalName = professionalName;
        this.customerName = customerName;
        this.appointmentDateTime = appointmentDateTime;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.appUrl = appUrl;
        this.reasonRejected = reasonReject;
    }

    public EmailRequestDTO() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getReasonRejected() {
        return reasonRejected;
    }

    public void setReasonRejected(String reasonRejected) {
        this.reasonRejected = reasonRejected;
    }
}
