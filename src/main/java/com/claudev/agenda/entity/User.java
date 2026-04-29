package com.claudev.agenda.entity;

import com.claudev.agenda.enums.Role;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank
    @Size(max = 50)
    @Column(name="first_name")
    private String firstName;

    //@NotBlank
    @Size(max = 50)
    @Column(name="last_name")
    private String lastName;

    //@NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    //@NotBlank
    @Size(min = 8, max = 255)
    @Column
    private  String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Nullable
    @Size(max = 255)
    @Column(name = "job_title")
    private String jobTitle;

    @Nullable
    @Size(max = 255)
    @Column(name = "vat_registration_number")
    private String vatRegistrationNumber;

    @Nullable
    private boolean remote;

    @Nullable
    @Column(name = "on_site")
    private boolean onSite;


    @Size(max = 20)
    @Column(name = "phone_number")
    private String phoneNumber;


    //@NotBlank
    @Email
    @Size(max = 100)
    @Column( unique = true)
    private String email;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    @Column(name = "payment_methods")
    private List<PaymentMethods> paymentMethods;

    @Column(name = "profile_photo" , nullable = true ,length = 64)
    private String profilePhoto;

    @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "professional" ,cascade = CascadeType.ALL)
    private List<Appointment> appointmentProfessional;

    @OneToMany(mappedBy = "customer" ,cascade = CascadeType.ALL)
    private List<Appointment> appointmentAsCustomer;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PaymentMethods> paymentMethod;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Nullable
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(@Nullable String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Nullable
    public String getVatRegistrationNumber() {
        return vatRegistrationNumber;
    }

    public void setVatRegistrationNumber(@Nullable String vatRegistrationNumber) {
        this.vatRegistrationNumber = vatRegistrationNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PaymentMethods> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethods> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Appointment> getAppointmentProfessional() {
        return appointmentProfessional;
    }

    public void setAppointmentProfessional(List<Appointment> appointmentProfessional) {
        this.appointmentProfessional = appointmentProfessional;
    }

    public List<Appointment> getAppointmentAsCustomer() {
        return appointmentAsCustomer;
    }

    public void setAppointmentAsCustomer(List<Appointment> appointmentAsCustomer) {
        this.appointmentAsCustomer = appointmentAsCustomer;
    }

    public List<PaymentMethods> getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(List<PaymentMethods> paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordTokenExpiry() {
        return resetPasswordTokenExpiry;
    }

    public void setResetPasswordTokenExpiry(LocalDateTime resetPasswordTokenExpiry) {
        this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
    }
}
