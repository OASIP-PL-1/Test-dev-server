package com.example.backend.repositories;

import com.example.backend.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository <Event, Integer>, CrudRepository<Event, Integer> {
    public List<Event> findByEventCategoryIdOrderByEventStartTimeDesc(Integer id);
    public List<Event> findByEventStartTimeLessThanOrderByEventStartTimeDesc(Date currentDate);
    public List<Event> findByEventStartTimeGreaterThanOrderByEventStartTimeAsc(Date currentDate);
    public List<Event> findByEventStartTimeGreaterThanAndEventStartTimeLessThan(Date starDateTime, Date endDateTime);
    public List<Event> findAllByEventStartTimeBetween(Date startTime, Date endTime);
    public List<Event> findAllByEventStartTimeBetweenOrEventStartTimeBetween(Date startTime, Date endTime, Date startTimeMinus, Date endTimeMinus);
    public Event findTopByOrderByIdDesc();


}
