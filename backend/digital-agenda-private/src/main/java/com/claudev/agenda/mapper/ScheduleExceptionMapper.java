package com.claudev.agenda.mapper;

import com.claudev.agenda.dto.ScheduleExceptionRequestDTO;
import com.claudev.agenda.dto.ScheduleExceptionResponseDTO;
import com.claudev.agenda.entity.ScheduleException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleExceptionMapper {

    @Mapping(target = "id" ,  ignore = true)
    @Mapping(target = "professional", ignore = true)
    ScheduleException toEntity (ScheduleExceptionRequestDTO scheduleExceptionRequestDTO);

    ScheduleExceptionResponseDTO toResponseDTO (ScheduleException scheduleException);
}
