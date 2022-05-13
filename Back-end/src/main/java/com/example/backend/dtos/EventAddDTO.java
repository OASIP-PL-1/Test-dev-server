package com.example.backend.dtos;

import com.example.backend.entities.Event;
import com.example.backend.entities.EventCategory;
import com.example.backend.repositories.EventCategoryRepository;
import com.example.backend.repositories.EventRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventAddDTO {
    private int id;
    private String bookingName;
    private String bookingEmail;
    private Date startTime;
    private String notes;
    private int eventCategoryId;
}
