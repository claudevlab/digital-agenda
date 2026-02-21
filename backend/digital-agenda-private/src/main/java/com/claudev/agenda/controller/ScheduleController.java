package com.claudev.agenda.controller;

import com.claudev.agenda.dto.ScheduleRequestDTO;
import com.claudev.agenda.dto.ScheduleResponseDTO;
import com.claudev.agenda.entity.Schedule;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.mapper.ScheduleMapper;
import com.claudev.agenda.repository.UserRepository;
import com.claudev.agenda.service.ScheduleService;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final ScheduleMapper scheduleMapper;

    public ScheduleController(ScheduleService scheduleService, UserService userService, ScheduleMapper scheduleMapper) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.scheduleMapper = scheduleMapper;
    }

    /*
    @PathVariable il parametro si trova dentro l'URL ovvero fa parte dell'indirizzo
    si utilizza quando vogliamo identificare una risorsa specifica

     */
    @PostMapping("/{professionalId}")
    public ResponseEntity<ScheduleResponseDTO> createSchedule (
            @Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO,
            @PathVariable Long professionalId) {

        // recupero user (professionista o lav autonomo)
        User professionalUser = userService.getUserById(professionalId).orElseThrow( () -> new RuntimeException("professinista non trovato" ));

        // conversione del DTO -> enity con il mapper
        Schedule schedule = scheduleMapper.toEntity(scheduleRequestDTO);
        schedule.setUser(professionalUser);

        // business logic
        Schedule scheduleSaved = scheduleService.createSchedule(schedule,professionalUser);

        System.out.println("ID generato " + scheduleSaved.getId());
        System.out.println("giorno" + scheduleSaved.getDayOfWeek());
// entity -> responseDTO (return 201)
    return ResponseEntity.status(HttpStatus.CREATED).body(scheduleMapper.toResponseDTO(scheduleSaved));
}
/*
@GetMapping("/{professionalId}")
public ResponseEntity<List<ScheduleResponseDTO>> getSchedules (@PathVariable Long professionalId) {
        User professionalUser = userService.getUserById(professionalId).orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        List<Schedule> schedules = scheduleService.getSchedulesByProfessional(professionalUser);

        return ResponseEntity.ok(scheduleMapper.toRespondeDTOList(schedules));
}

 */


}

