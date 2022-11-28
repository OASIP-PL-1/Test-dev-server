package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDTO {
    @Min(value = 1, message = "The least id number is 1.")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "Start time is required.")
    @Future(message = "Selected date must be in the future.")
    private Date startTime;

//    @NotBlank(message = "Event's note is required!")
    @Size(max = 500, message = "Event's note is too long, maximum 500 characters.")
    private String notes;

    @Size(max = 85, message = "Event's attachment name is too long, maximum 85 characters.")
    private String eventAttachmentName;
}
