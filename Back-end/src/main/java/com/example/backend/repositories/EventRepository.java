package com.example.backend.repositories;

import com.example.backend.entities.Event;
import com.example.backend.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository <Event, Integer>, CrudRepository<Event, Integer> {
    public List<Event> findByEventCategoryIdOrderByEventStartTimeDesc(Integer id);
    public List<Event> findByEventStartTimeGreaterThanAndEventStartTimeLessThan(Date starDateTime, Date endDateTime);
    public Event findTopByOrderByIdDesc();

    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTIme, INTERVAL e.eventDuration MINUTE) <= :currentTime",
            nativeQuery = true)
    public List<Event> filterPastEvent(@Param("currentTime")Date currentTime);

    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTIme, INTERVAL e.eventDuration MINUTE) >= :currentTime",
            nativeQuery = true)
    public List<Event> filterUpcomingEvent(@Param("currentTime")Date currentTime);

    @Query(value = "select distinct e.* from events e where (e.eventCategoryId = :eventCategoryId)" +
            "AND ((:startTime > e.eventStartTime AND :startTime < DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE))" +
            "OR (:endTime > e.eventStartTime AND :endTime < DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE))" +
            "OR (e.eventStartTime > :startTime AND e.eventStartTime < :endTime)" +
            "OR (DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) > :startTime AND DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) < :endTime)" +
            "OR ((:startTime = e.eventStartTime) OR (:endTime = DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE))))",
            nativeQuery = true)
    public List<Event> overlap(
            @Param("eventCategoryId") Integer categoryId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );

}
