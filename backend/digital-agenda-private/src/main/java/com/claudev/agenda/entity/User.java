package com.claudev.agenda.entity;

import com.claudev.agenda.enums.Role;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name="first_name",nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name="last_name",nullable = false)
    private String lastName;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(nullable = false)
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

    @NotBlank
    @Size(max = 20)
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true)
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

    public @NotBlank @Size(max = 50) String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank @Size(max = 50) String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank @Size(max = 50) String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank @Size(max = 50) String lastName) {
        this.lastName = lastName;
    }

    public @NotBlank @Size(max = 50) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(max = 50) String username) {
        this.username = username;
    }

    public @NotBlank @Size(min = 8, max = 255) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 8, max = 255) String password) {
        this.password = password;
    }

    public @NotBlank @Size(max = 20) String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotBlank @Size(max = 20) String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public @NotBlank @Email @Size(max = 100) String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Email @Size(max = 100) String email) {
        this.email = email;
    }

    public @NotNull Role getRole() {
        return role;
    }

    public void setRole(@NotNull Role role) {
        this.role = role;
    }

    public @Size(min = 8, max = 255) String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(@Size(min = 8, max = 255) String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public @Size(min = 8, max = 255) String getVatRegistrationNumber() {
        return vatRegistrationNumber;
    }

    public void setVatRegistrationNumber(@Size(min = 8, max = 255) String vatRegistrationNumber) {
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

    public List<PaymentMethods> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethods> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && role == user.role && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, role, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", onSite=" + onSite +
                ", remote=" + remote +
                ", vatRegistrationNumber='" + vatRegistrationNumber + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", role=" + role +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", id=" + id +
                '}';
    }
}
