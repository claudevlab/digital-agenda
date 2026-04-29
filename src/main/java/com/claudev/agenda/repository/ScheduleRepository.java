package com.claudev.agenda.repository;

import com.claudev.agenda.entity.Schedule;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    List<Schedule> findByUser (User user);
    List<Schedule> findByUserAndDayOfWeek (User user , DayOfWeek dayOfWeek);

}
