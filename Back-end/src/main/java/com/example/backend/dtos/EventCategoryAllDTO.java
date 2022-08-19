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
    private String categoryName;

    @NotBlank(message = "Event category's description is required.")
    private String categoryDescription;

    @Min(value = 20, message = "Duration must greater than 1.")
    @NotNull(message = "Duration is required.")
    private int duration;
}
