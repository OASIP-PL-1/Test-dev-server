package com.example.backend.entities;

import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "eventCategoryOwners")
@Entity
@Setter @Getter
//@AllArgsConstructor @NoArgsConstructor
@ToString
public class EventCategoryOwner {
    @Id
    @Column(name = "eventCategoryOwnerId", nullable = false)
    private int Id;

//    @EmbeddedId
//    private EventCategoryId id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private EventCategory eventCategoryId;

    private class EventCategoryId implements Serializable{
        private long EventCategoryId;
    }

}