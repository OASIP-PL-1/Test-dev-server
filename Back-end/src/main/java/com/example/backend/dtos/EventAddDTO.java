package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
