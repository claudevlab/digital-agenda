package com.claudev.agenda.controller;

import com.claudev.agenda.dto.ScheduleExceptionRequestDTO;
import com.claudev.agenda.dto.ScheduleExceptionResponseDTO;
import com.claudev.agenda.entity.ScheduleException;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.mapper.ScheduleExceptionMapper;
import com.claudev.agenda.service.ScheduleExceptionService;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exceptions")
public class ScheduleExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleExceptionController.class);


    private final ScheduleExceptionService scheduleExceptionService;
    private final ScheduleExceptionMapper scheduleExceptionMapper;
    private final UserService userService;


    public ScheduleExceptionController(ScheduleExceptionService scheduleExceptionService, ScheduleExceptionMapper scheduleExceptionMapper, UserService userService) {
        this.scheduleExceptionService = scheduleExceptionService;
        this.scheduleExceptionMapper = scheduleExceptionMapper;
        this.userService = userService;
    }

    // POST /api/exceptions/{professionalId}
    @PostMapping("/{professionalId}")
    public ResponseEntity<ScheduleExceptionResponseDTO> createScheduleException (
        @Valid @RequestBody ScheduleExceptionRequestDTO scheduleExceptionRequestDTO,
                @PathVariable Long professionalId) {

        User professionalUser = userService.getUserById(professionalId).orElseThrow( () ->new RuntimeException("Professionista non trovato"));

        ScheduleException scheduleException = scheduleExceptionMapper.toEntity(scheduleExceptionRequestDTO);  // Da fixare | TO DO |

        ScheduleException scheduleExceptionSaved = scheduleExceptionService.createExceptionSchedule(scheduleException,professionalUser);

        logger.info("Eccezione creata per il gionro : {} - professionista id : {}", scheduleExceptionSaved.getDate(),professionalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleExceptionMapper.toResponseDTO(scheduleExceptionSaved));
    }

    // GET /api/exceptions/{professionalId
    @GetMapping("/{professionalId}")
    public ResponseEntity<List<ScheduleExceptionResponseDTO>> getScheduleExceptions (
            @PathVariable Long professionalId ) {

        User professionalUser = userService.getUserById(professionalId).orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        List<ScheduleException> exceptions =scheduleExceptionService.getSchedulesByProfessional(professionalUser);

        List<ScheduleExceptionResponseDTO> responseDTOList = exceptions.stream().map(scheduleExceptionMapper::toResponseDTO).toList();

        logger.info("Recuperate {} eccezzioni per il professinista ID : ", exceptions.size(),professionalUser.getId());

        return ResponseEntity.ok(responseDTOList);
    }

    // /api/exceptions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteException (@PathVariable Long id) {

        scheduleExceptionService.deleteScheduleException(id);

        logger.info("Eccezione con ID: {} eliminata", id);

        return ResponseEntity.noContent().build();


    }


}
