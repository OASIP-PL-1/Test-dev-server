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
public class EventDTO {
    private int id;
    private String bookingName;
    private String bookingEmail;
    private String categoryName;
    private int duration;
    private String notes;
    private Date startTime;
    private String eventAttachmentName;

    public ZonedDateTime getStartTime(){
        return ZonedDateTime.ofInstant(startTime.toInstant(), ZoneId.of("Asia/Bangkok"));
    }
}
