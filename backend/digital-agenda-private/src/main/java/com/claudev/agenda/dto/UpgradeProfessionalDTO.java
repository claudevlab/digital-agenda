package com.claudev.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpgradeProfessionalDTO {

    @NotBlank(message = "JobTitle e' un campo obbligatorio")
    private String jobTitle;

    private  String vatRegistrationNumber;

    @NotBlank(message = "Obbligatorio inserire un numero di telefono")
    @Size(max = 20)
    private String phoneNumber;

    private boolean remote;
    private boolean onSite;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getVatRegistrationNumber() {
        return vatRegistrationNumber;
    }

    public void setVatRegistrationNumber(String vatRegistrationNumber) {
        this.vatRegistrationNumber = vatRegistrationNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public boolean isOnSite() {
        return onSite;
    }

    public void setOnSite(boolean onSite) {
        this.onSite = onSite;
    }
}
