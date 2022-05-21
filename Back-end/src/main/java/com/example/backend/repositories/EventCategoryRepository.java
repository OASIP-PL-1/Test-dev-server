package com.example.backend.repositories;

import com.example.backend.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
    @Query(value ="select count(eventCategoryId) from eventCategories where eventCategoryId!=:categoryId and eventCategoryName=:categoryName", nativeQuery = true)
    public int checkExistedName(@Param("categoryId") Integer id, @Param("categoryName") String categoryName);


}