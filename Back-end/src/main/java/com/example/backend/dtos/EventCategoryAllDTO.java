package com.example.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCategoryAllDTO {
    private int id;
    private String categoryName;
    private String categoryDescription;
    private int duration;
}
