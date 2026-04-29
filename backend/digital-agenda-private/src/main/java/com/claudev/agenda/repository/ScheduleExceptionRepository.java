package com.claudev.agenda.repository;

import com.claudev.agenda.entity.ScheduleException;
import com.claudev.agenda.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

import java.util.List;

public interface ScheduleExceptionRepository extends JpaRepository<com.claudev.agenda.entity.ScheduleException,Long> {

    // trova tutte le eccezioni di un professionista
    List<ScheduleException> findByProfessional(User professional);

    // trova le eccezioni in un range di date ( potrebbe tornare utile)
    List<ScheduleException> findByProfessionalAndDateBetween (
            User professional,
            LocalDate start,
            LocalDate end
    );

    List<ScheduleException> findByProfessionalAndDate(User professional, LocalDate date);

    boolean existsByProfessionalAndDate(User professional , LocalDate date);
}
