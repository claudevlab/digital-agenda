package com.claudev.agenda.service;

import com.claudev.agenda.entity.ScheduleException;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.repository.ScheduleExceptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleExceptionService {

    private final ScheduleExceptionRepository scheduleExceptionRepository;

    public ScheduleExceptionService(ScheduleExceptionRepository scheduleExceptionRepository) {
        this.scheduleExceptionRepository = scheduleExceptionRepository;
    }

    @Transactional
    public ScheduleException createExceptionSchedule (ScheduleException scheduleException, User user) {

        // controllo sovrapposizioni
        List<ScheduleException> existingScheduleException = scheduleExceptionRepository.findByProfessionalAndDate(user,scheduleException.getDate());

        if (!existingScheduleException.isEmpty()) {
            throw new IllegalArgumentException("Esiste gia' un eccezione per questo giorno");
        }

        scheduleException.setProfessional(user);
        ScheduleException saved =scheduleExceptionRepository.save(scheduleException);
        return saved;
    }

    public List<ScheduleException> getSchedulesByProfessional (User professional) {
        return scheduleExceptionRepository.findByProfessional(professional);
    }

    @Transactional
    public void deleteScheduleException (Long id) {
        scheduleExceptionRepository.deleteById(id);
    }

    public boolean isExceptionDate (User professional , LocalDate date) {
        return scheduleExceptionRepository.existsByProfessionalAndDate(professional, date);
    }
}
