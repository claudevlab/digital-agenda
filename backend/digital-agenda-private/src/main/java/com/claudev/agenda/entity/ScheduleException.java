package com.claudev.agenda.entity;

import jakarta.persistence.*;


import java.time.LocalDate;

@Entity
public class ScheduleException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    private LocalDate date; // giorno da bloccare
    private String reason; // opzionale - esempio "motivi personali"

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional; // quale professionista appartiene

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public User getProfessional() {
        return professional;
    }

    public void setProfessional(User professional) {
        this.professional = professional;
    }
}
