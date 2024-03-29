package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventAllDTO {
    private int id;
    private String bookingName;
    private String categoryName;
    private Date startTime;
    private int duration;


    public ZonedDateTime getStartTime(){
        return ZonedDateTime.ofInstant(startTime.toInstant(), ZoneId.of("Asia/Bangkok"));
    }
}
