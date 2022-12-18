package com.example.backend.repositories;

import com.example.backend.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository <Event, Integer>, CrudRepository<Event, Integer> {
    public List<Event> findByEventCategoryIdOrderByEventStartTimeDesc(int id);
    public List<Event> findByEventCategoryIdAndAndBookingEmailOrderByEventStartTime(int categoryId, String email);

    public List<Event> findByEventStartTimeBetweenOrderByEventStartTimeAsc(Date startDateTime, Date endDateTime);
    public List<Event> findByEventStartTimeBetweenAndBookingEmailOrderByEventStartTimeAsc(Date startDateTime, Date endDateTime, String studentEmail);

    @Query(value="select e.* " +
            "from events e JOIN eventCategoryOwners eco ON e.eventCategoryId = eco.eventCategoryId " +
            "where e.eventStartTime >= :startDateTime AND e.eventStartTime <= :endDateTime AND eco.userId = :lecturerId",
            nativeQuery = true)
    public List<Event> lecturerFilterByDate(@Param("startDateTime") Date startDateTime, @Param("endDateTime") Date endDateTime, @Param("lecturerId") int lecturerId);

    public Event findTopByOrderByIdDesc();
    public List<Event> findByEventCategoryIdAndEventStartTimeBetweenOrderByEventStartTimeAsc(int id, Date startTime, Date endTime);
    public List<Event> findByBookingEmail(String userEmail);

    @Query(value = "select e.* from (events e JOIN eventCategoryOwners eco ON e.eventCategoryId = eco.eventCategoryId) JOIN users u on eco.userId = u.userId " +
            "where u.userEmail = :userEmail",
            nativeQuery = true)
    public List<Event> lecturerGetEvent(@Param("userEmail") String userEmail);

    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) <= :currentTime " +
            "order by e.eventStartTime desc",
            nativeQuery = true)
    public List<Event> filterPastEvent(@Param("currentTime")Date currentTime);

    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) <= :currentTime AND e.bookingEmail = :studentEmail " +
            "order by e.eventStartTime desc",
            nativeQuery = true)
    public List<Event> studentFilterPastEvent(@Param("currentTime")Date currentTime, @Param("studentEmail")String StudentEmail);

    @Query(value="select e.* " +
            "from events e JOIN eventCategoryowners eco ON e.eventCategoryId = eco.eventCategoryId " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) <= :currentTime AND eco.userId=:lecturerId " +
            "order by e.eventStartTime desc;",
            nativeQuery = true)
    public List<Event> lecturerFilterPastEvent(@Param("currentTime")Date currentTime, @Param("lecturerId")int lecturerId);


    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) >= :currentTime " +
            "order by e.eventStartTime asc",
            nativeQuery = true)
    public List<Event> filterUpcomingEvent(@Param("currentTime")Date currentTime);

    @Query(value = "select e.* from events e " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) >= :currentTime AND e.bookingEmail = :studentEmail " +
            "order by e.eventStartTime asc",
            nativeQuery = true)
    public List<Event> studentFilterUpcomingEvent(@Param("currentTime")Date currentTime, @Param("studentEmail")String StudentEmail);

    @Query(value="select e.* " +
            "from events e JOIN eventCategoryowners eco ON e.eventCategoryId = eco.eventCategoryId " +
            "where DATE_ADD(e.eventStartTime, INTERVAL e.eventDuration MINUTE) >= :currentTime AND eco.userId=:lecturerId " +
            "order by e.eventStartTime desc;",
            nativeQuery = true)
    public List<Event> lecturerFilterUpcomingEvent(@Param("currentTime")Date currentTime, @Param("lecturerId")int lecturerId);

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

    @Query(value="select count(*)\n" +
            "from events e JOIN eventCategoryOwners e2 on e.eventCategoryId = e2.eventCategoryId\n" +
            "where e.eventId = :eventId and e2.userId = :userId",
        nativeQuery = true)
    public int checkLecturerCategory(@Param("eventId")int eventId,
                                     @Param("userId")int userId);

}
