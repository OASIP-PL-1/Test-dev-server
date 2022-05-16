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
    public List<Event> findByEventStartTimeLessThanOrderByEventStartTimeDesc(Date currentDate);
    public List<Event> findByEventStartTimeGreaterThanOrderByEventStartTimeAsc(Date currentDate);
    public List<Event> findByEventStartTimeGreaterThanAndEventStartTimeLessThan(Date starDateTime, Date endDateTime);
    public List<Event> findAllByEventStartTimeBetween(Date startTime, Date endTime);
    public List<Event> findAllByEventStartTimeBetweenOrEventStartTimeBetweenAndEventCategory(Date startTime, Date endTime, Date startTimeMinus, Date endTimeMinus, EventCategory eventCategory);
    public List<Event> findAllByEventStartTimeBetweenOrEventStartTimeBetween(Date startTime, Date endTime, Date startTimeMinus, Date endTimeMinus);
    public Event findTopByOrderByIdDesc();
    public List<Event> findAllByEventCategoryId(int id);

//    @Query (value = "select e from Event e")
//    public boolean test();
    @Query(value = "select e from Event e where (e.eventCategory = :eventCategory) AND ((e.eventStartTime BETWEEN :startTime AND :endTime) OR (e.eventStartTime BETWEEN :startTimeMinus AND :startTime))")
    public List<Event> overlap(
            @Param("eventCategory")EventCategory eventCategory,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("startTimeMinus") Date startTimeMinus);
}
