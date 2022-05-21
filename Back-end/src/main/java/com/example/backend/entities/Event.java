package com.example.backend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    private String bookingName;

    @Column(name = "bookingEmail", nullable = false, length = 50)
    private String bookingEmail;

    @Column(name = "eventStartTime", nullable = false)
    private Date eventStartTime;

    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @Lob
    @Column(name = "eventNotes" , length = 500)
    private String eventNotes;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private EventCategory eventCategory;

}