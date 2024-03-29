package com.example.backend.dtos;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventAddDTO {
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Booking's name is required.")
    @Size(max = 100, message = "Booking's name is too long, maximum 100 characters.")
    private String bookingName;

    @NotBlank(message = "Booking's email is required.")
    @Size(max = 50, message = "Booking's email is too long, maximum 50 characters.")
    @Email(message = "Email is not valid.")
    private String bookingEmail;

    @NotNull(message = "Start time is required.")
    @Future(message = "Selected date must be in the future.")
    private Date startTime;

    @Size(max = 500, message = "Event's note is too long, maximum 500 characters.")
    private String notes;

    @NotNull(message = "Event category's id is required.")
    private int eventCategoryId;

    @Size(max = 85, message = "Event's attachment name is too long, maximum 85 characters.")
    private String eventAttachmentName;
}
