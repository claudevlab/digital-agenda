package com.claudev.agenda.mapper;

import com.claudev.agenda.dto.ScheduleRequestDTO;
import com.claudev.agenda.dto.ScheduleResponseDTO;
import com.claudev.agenda.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(target = "id" ,  ignore = true)
    @Mapping(target = "user", ignore = true )
    Schedule toEntity (ScheduleRequestDTO scheduleRequestDTO);

    // da entity -> DTO , rispondiamo al frontend
    @Mapping(target = "professionalId", source = "user.id")
    ScheduleResponseDTO toResponseDTO (Schedule schedule);

    // mapping di una lista , mapStruct lo fa in automatico
    List<ScheduleResponseDTO> toRespondeDTOList (List<Schedule> entityList);


}
