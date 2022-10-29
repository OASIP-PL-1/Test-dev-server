package com.example.backend.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.RowId;

import javax.persistence.*;

//@Table(name = "eventCategoryOwners", indexes = {
//        @Index(name = "fk_eventCategoryOwners_eventCategories1_idx", columnList = "eventCategoryId"),
//        @Index(name = "fk_eventCategoryOwners_users1_idx", columnList = "userId")
//})
@Table(name = "eventCategoryOwners")
@Entity
@Getter @Setter
public class EventCategoryOwner {
    @Id
    @Column(name = "eventCategoryOwnerId", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private EventCategory eventCategory;

}