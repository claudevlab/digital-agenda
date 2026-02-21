package com.claudev.agenda.mapper;

import com.claudev.agenda.dto.AppointmentRequestDTO;
import com.claudev.agenda.dto.AppointmentResponseDTO;
import com.claudev.agenda.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    // request -> entity
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "professional" , ignore = true)
    @Mapping(target = "customer" , ignore = true)
    @Mapping(target = "status" , ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity (AppointmentRequestDTO appointmentRequestDTO);

    // entity -> reponse
    // navighiamo nella classe e gli diamo il nome
    @Mapping(target = "professionalId", source = "professional.id")
    @Mapping(target = "professionalName", expression = "java(entity.getProfessional().getFirstName() + ' ' + entity.getProfessional().getLastName())")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(entity.getCustomer().getFirstName() + ' ' + entity.getCustomer().getLastName())")
    AppointmentResponseDTO toResponseDTO(Appointment entity);


}
