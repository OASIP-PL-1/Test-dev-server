package com.example.backend.repositories;

import com.example.backend.entities.EventCategoryOwner;
import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {
    @Transactional
    @Modifying(clearAutomatically = true)
    public void deleteByUser(User user);

    public EventCategoryOwner findByUserIdAndEventCategoryId(int userId, int categoryId);

}
