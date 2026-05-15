package com.claudev.agenda.controller;

import com.claudev.agenda.dto.AppointmentRequestDTO;
import com.claudev.agenda.dto.AppointmentResponseDTO;
import com.claudev.agenda.entity.Appointment;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.AppointmentStatus;
import com.claudev.agenda.mapper.AppointmentMapper;
import com.claudev.agenda.service.AppointmentService;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {


    private final UserService userService;
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;


    public AppointmentController(UserService userService, AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
    }

    /*
    @RequestParam --> il parametro che ci interessa si trova alla fine dell'URL dopo il punto iterrogativo
    si utilizza per filtrare,ordinare,passare dati aggiuntivi
     */
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment (
            @Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO,
            Authentication authentication) {

        // recupero customer
        String customerEmail = authentication.getName(); // injection dati token
        User customer = userService.getUserByEmail(customerEmail).orElseThrow( () ->new RuntimeException("Cliente non trovato"));

        // recupero professional dal DTO
        User professional = userService.getUserById(appointmentRequestDTO.getProfessionalId()).orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        // conversione DTO - > Entity
        Appointment appointmentEntity = appointmentMapper.toEntity(appointmentRequestDTO);

        // colleghiamo gli utenti
        appointmentEntity.setProfessional(professional);
        appointmentEntity.setCustomer(customer);

        // logica di business
        Appointment appointmentSaved = appointmentService.createAppointment(appointmentEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentMapper.toResponseDTO(appointmentSaved));
    }

    // GET /api/appointments/customer-appointments
    // cliente che vede i propri appuntamenti
    @GetMapping("/customer-appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getCustomerAppointments (Authentication authentication) {

        String email = authentication.getName();
        User customer = userService.getUserByEmail(email).orElseThrow(() -> new RuntimeException("Cliente non trovato"));

        return ResponseEntity.ok(appointmentService.getAppointmentByCustomerOrder(customer));
    }

    // GET /api/appointments/professional-appointments
    // Il professionista vede i propri appuntamenti
    @GetMapping("/professional-appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getProfessionalAppointments (Authentication authentication) {

        String email = authentication.getName();
        User professional = userService.getUserByEmail(email).orElseThrow(() -> new RuntimeException("Professionista non trovato"));


        return  ResponseEntity.ok(appointmentService.getAppointmentByProfessionalOrder(professional));

    }

    // PUT /api/appointments/{id}/status
    // Il professionista accetta o rifiuta
    @PatchMapping("/{id}/status") // patch lo utilizziamo le modifiche parziali
    public ResponseEntity<AppointmentResponseDTO> updateStatus (@PathVariable Long id ,
                                                                @RequestParam AppointmentStatus status,
                                                                @RequestParam(required = false) String reasonRejected) {
        Appointment updated = appointmentService.updateStatus(id,status,reasonRejected);
        return ResponseEntity.ok(appointmentMapper.toResponseDTO(updated));
    }

    // DELETE /api/appointments/{id}
    // Cancella (disdice) un appuntamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment (@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }



}
