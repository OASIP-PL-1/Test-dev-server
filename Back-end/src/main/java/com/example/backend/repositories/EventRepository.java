package com.example.backend.repositories;

import com.example.backend.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository <Event, Integer>, CrudRepository<Event, Integer> {
    public List<Event> findByEventCategoryIdOrderByEventStartTimeDesc(int id);
    public List<Event> findByEventStartTimeBetweenOrderByEventStartTimeAsc(Date startDateTime, Date endDateTime);
    public Event findTopByOrderByIdDesc();
    public List<Event> findByEventCategoryIdAndEventStartTimeBetweenOrderByEventStartTimeAsc(int id, Date startTime, Date endTime);

//    @Query(value="select e.* from Events e " +
//            "where e.eventStartTime >= :startDateTime AND e.eventStartTime < :endDateTime",
//            nativeQuery = true)
//    public List<Event> filterSelectDate(
//            @Param("startDateTime") Date startDateTime,
//            @Param("endDateTime") Date endDateTime
//            );

    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) <= :currentTime " +
            "order by e.eventStartTime desc",
            nativeQuery = true)
    public List<Event> filterPastEvent(@Param("currentTime")Date currentTime);

    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) >= :currentTime " +
            "order by e.eventStartTime asc",
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
            @Param("eventCategoryId") int categoryId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );

}
