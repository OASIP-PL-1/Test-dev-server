package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCategoryAllDTO {
    @Min(value = 1, message = "The least id number is 1.")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Event category's name is required.")
    @Size(max = 100, message = "Event category's name is too long, maximum 100 characters.")
    private String categoryName;

    @NotBlank(message = "Event category's description is required.")
    @Size(max = 500, message = "Event category's description is too long, maximum 500 characters.")
    private String categoryDescription;

    @Min(value = 1, message = "Duration must greater than 1.")
    @Max(value = 480, message = "Duration's maximum is 480 minutes.")
    @NotNull(message = "Duration is required.")
    private int duration;
}
