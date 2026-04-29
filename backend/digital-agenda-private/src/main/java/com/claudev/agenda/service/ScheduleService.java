package com.claudev.agenda.service;

import com.claudev.agenda.entity.Schedule;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.DayOfWeek;
import com.claudev.agenda.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Schedule createSchedule (Schedule schedule,User user) {
        // validazione orario di fine < orario di fine
        if (!schedule.getStartTime().isBefore(schedule.getEndTime())) {
            throw new IllegalArgumentException("L'orario di inizio deve essere strettamente precedente all'orario di fine.");
        }

        // controllo sovrapposizioni
        List<Schedule> existingSchedule = scheduleRepository.findByUserAndDayOfWeek(user, schedule.getDayOfWeek());

        for (Schedule existing : existingSchedule) {
            boolean isOverlapping =
                    schedule.getStartTime().isBefore(existing.getEndTime()) &&
                            schedule.getEndTime().isAfter(existing.getStartTime());

            if (isOverlapping) {
                throw new IllegalArgumentException("esiste gia' uno slot che si sovrappone a questo orario");
            }
        }

        // associazione user
        schedule.setUser(user);
        Schedule saved = scheduleRepository.save(schedule);
        logger.info("Schedule salvato con ID : {}" , saved.getId());
        return saved;
    }

    // metodo per recuperare gli orari in base all'user professional
    public List<Schedule> getSchedulesByProfessional (User professional) {
        return scheduleRepository.findByUser(professional);
    }


    // da verificare (chiedi )`
    public List<Schedule> getScheduleByUserAndDayOfWeek (User professional , DayOfWeek dayOfWeek) {
        return scheduleRepository.findByUserAndDayOfWeek(professional, dayOfWeek);
    }


    @Transactional
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
