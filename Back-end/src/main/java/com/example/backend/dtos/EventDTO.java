package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private int id;
    private String bookingName;
    private String bookingEmail;
    private String categoryName;
    private int duration;
    private String notes;
    private Instant startTime;
}
