package com.example.backend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "eventId", nullable = false)
    private Integer id;

    @Column(name = "bookingName", nullable = false, length = 100)
//    @NotBlank(message = "Event's name is required.")
//    @Size(max = 100, message = "Booking name is too long, limit 100 characters")
    private String bookingName;

    @Column(name = "bookingEmail", nullable = false, length = 50)
//    @NotBlank(message = "Booking's email is required.")
//    @Size(max = 50, message = "Email is too long, limit 50 characters.")
//    @Email(message = "An email is invalid.")
    private String bookingEmail;

    @Column(name = "eventStartTime", nullable = false)
//    @NotNull(message = "Start time must be choosen.")
//    @Future(message = "Event's start time can't be in the past.")
    private Date eventStartTime;

    @Column(name = "eventDuration", nullable = false)
//    @NotNull(message = "Event duration is required.")
//    @Min(value = 1, message = "Event's duration must longer than 0 minutes")
//    @Max(value = 480, message = "Event's duration can't be longer than 480 minutes")
    private Integer eventDuration;

    @Lob
    @Column(name = "eventNotes" , length = 500)
    @Size(max = 500, message = "Event's note is too long, limit")
    private String eventNotes;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
//    @NotNull(message = "Event category must be choosen")
    private EventCategory eventCategory;

}