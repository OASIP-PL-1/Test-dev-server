package com.example.backend.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@Entity
@Table(name = "eventCategories")
public class EventCategory {
    @Id
    @Column(name = "eventCategoryId", nullable = false)
    private Integer id;

    @NotBlank(message = "Event category's name is required.")
    @Column(name = "eventCategoryName", nullable = false, length = 100)
    private String eventCategoryName;

    @Lob
    @Column(name = "eventCategoryDescription", length = 500)
    @NotBlank(message = "Event category's description is required.")
    private String eventCategoryDescription;

    @Column(name = "eventCategoryDuration", nullable = false)
    @Min(value = 20, message = "Duration must greater than 1.")
    @NotNull(message = "Duration is required.")
    private Integer eventCategoryDuration;
}
