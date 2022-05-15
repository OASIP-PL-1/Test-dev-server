package com.example.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventAllDTO {
    private Integer id;
    private String bookingName;
    private String categoryName;
    private Date startTime;
    private int duration;


    public ZonedDateTime getStartTime(){
        return ZonedDateTime.ofInstant(startTime.toInstant(), ZoneId.of("Asia/Bangkok"));
    }
}
