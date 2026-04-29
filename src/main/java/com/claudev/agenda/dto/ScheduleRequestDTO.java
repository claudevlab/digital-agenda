package com.claudev.agenda.dto;

import com.claudev.agenda.enums.DayOfWeek;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/*
       INPUT DAL FRONTED
 */

public class ScheduleRequestDTO {

    @NotNull(message = "Il giorno della settimana e'obbligatorio")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "L'orario di inizio e'obbligatorio")
    private LocalTime startTime;

    @NotNull(message = "L'orario di fine e'obbligatorio")
    private LocalTime endTime;

    private String description;

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
