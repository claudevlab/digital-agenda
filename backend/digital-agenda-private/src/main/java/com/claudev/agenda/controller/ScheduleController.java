package com.claudev.agenda.controller;

import com.claudev.agenda.dto.AvailableSlotDTO;
import com.claudev.agenda.dto.ScheduleRequestDTO;
import com.claudev.agenda.dto.ScheduleResponseDTO;
import com.claudev.agenda.entity.Schedule;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.DayOfWeek;
import com.claudev.agenda.mapper.ScheduleMapper;

import com.claudev.agenda.service.AppointmentService;
import com.claudev.agenda.service.ScheduleExceptionService;
import com.claudev.agenda.service.ScheduleService;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    public final static Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final ScheduleMapper scheduleMapper;
    private final AppointmentService appointmentService;
    private final ScheduleExceptionService scheduleExceptionService;

    public ScheduleController(ScheduleService scheduleService, UserService userService, ScheduleMapper scheduleMapper , AppointmentService appointmentService,
                              ScheduleExceptionService scheduleExceptionService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.scheduleMapper = scheduleMapper;
        this.appointmentService = appointmentService;
        this.scheduleExceptionService = scheduleExceptionService;
    }

    /*
    @PathVariable il parametro si trova dentro l'URL ovvero fa parte dell'indirizzo
    si utilizza quando vogliamo identificare una risorsa specifica

     */

    // crea lo schedule
    @PostMapping("/{professionalId}")
    public ResponseEntity<ScheduleResponseDTO> createSchedule(
            @Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO,
            @PathVariable Long professionalId) {

        // recupero user (professionista o lav autonomo)
        User professionalUser = userService.getUserById(professionalId).orElseThrow(() -> new RuntimeException("professinista non trovato"));

        // conversione del DTO -> enity con il mapper
        Schedule schedule = scheduleMapper.toEntity(scheduleRequestDTO);
        schedule.setUser(professionalUser);

        // business logic
        Schedule scheduleSaved = scheduleService.createSchedule(schedule, professionalUser);

        // logf4 piu' professionale rispetti ai system out
        logger.info("Schedule creato con ID: {} per il giorno: {}",
                scheduleSaved.getId(), scheduleSaved.getDayOfWeek());

// entity -> responseDTO (return 201)
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleMapper.toResponseDTO(scheduleSaved));
    }

    // ottiene lo schedule
    @GetMapping("/{professionalId}")
    public ResponseEntity<List<ScheduleResponseDTO>> getSchedules(@PathVariable Long professionalId) {
        User professionalUser = userService.getUserById(professionalId).orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        List<Schedule> schedules = scheduleService.getSchedulesByProfessional(professionalUser);

        logger.info("Recuperati {} schedule per il professionista ID: {}",
                schedules.size(),
                professionalId);

        return ResponseEntity.ok(scheduleMapper.toRespondeDTOList(schedules));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);

        logger.info("Schedule con ID: {} eliminato", scheduleId);

        return ResponseEntity.noContent().build();
    }

    // mostriamo al frontend i slot disponibili e contrassegniamo quelli occupati
    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(
            @RequestParam Long professionalId,
            @RequestParam String date,
            @RequestParam(defaultValue = "60") int durationMinutes) {

        logger.info("available-slots: professionalId={}, date={}, durationMinutes={}", professionalId, date, durationMinutes);

        if (durationMinutes <= 0 || durationMinutes > 400) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        // trova il professionista
        User professional = userService.getUserById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        // converti la data
        LocalDate localDate = LocalDate.parse(date);

        // ---- controllo eccezioni di calendario (giorni esclusi) ----
        if (scheduleExceptionService.isExceptionDate(professional, localDate)) {
            logger.info("Richiesta slot per il {}: è un giorno di eccezione per il professionista {}. Restituisco lista vuota.",
                    localDate, professionalId);
            return ResponseEntity.ok(new ArrayList<>());
        }
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(localDate.getDayOfWeek().name());

        // prendi gli slot di disponibilità del professionista per quel giorno della settimana
        List<Schedule> schedules = scheduleService.getScheduleByUserAndDayOfWeek(professional, dayOfWeek);

        // ottieni gli slot già occupati (start, end) per quella data
        List<LocalDateTime[]> occupiedSlots = appointmentService.getOccupiedSlotsWithDuration(professional, localDate);

        List<AvailableSlotDTO> result = new ArrayList<>();

        for (Schedule schedule : schedules) {
            LocalTime current = schedule.getStartTime();
            LocalTime end = schedule.getEndTime();

            while (!current.plusMinutes(durationMinutes).isAfter(end)) {
                LocalTime slotEnd = current.plusMinutes(durationMinutes);

                LocalDateTime slotStartDateTime = localDate.atTime(current);
                LocalDateTime slotEndDateTime = localDate.atTime(slotEnd);

                boolean isOccupied = occupiedSlots.stream().anyMatch(occupied -> {
                    LocalDateTime occStart = occupied[0];
                    LocalDateTime occEnd = occupied[1];
                    // sovrapposizione: startSlot < endOcc && endSlot > startOcc
                    return slotStartDateTime.isBefore(occEnd) && slotEndDateTime.isAfter(occStart);
                });

                result.add(new AvailableSlotDTO(
                        current.toString().substring(0, 5),
                        slotEnd.toString().substring(0, 5),
                        !isOccupied
                ));

                current = slotEnd;
            }
        }

        return ResponseEntity.ok(result);
    }


}

