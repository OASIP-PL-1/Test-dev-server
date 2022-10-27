package com.example.backend.repositories;

import antlr.collections.List;
import com.example.backend.entities.EventCategory;
import com.example.backend.entities.EventCategoryOwner;
import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {
    public EventCategoryOwner findByUserIdAndEventCategoryId(User user, EventCategory category);
//    public EventCategoryOwner findByUserId(int userId);

    public EventCategoryOwner findByUserId(User user);

//    @Query(value="SELECT * FROM eventCategoryOwners WHERE userId = :userId and eventCategoryId = :eventCategoryId",
//            nativeQuery = true)
//    public EventCategoryOwner findMatchUserId(@Param("userId") int userId,
//                                              @Param("categoryId") int categoryId);
}
