package com.example.backend.repositories;

import com.example.backend.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
    @Query(value ="SELECT COUNT(eventCategoryId) FROM eventcategories where eventCategoryId!=:categoryId AND eventCategoryName=:categoryName", nativeQuery = true)
    public int checkExistedName(@Param("categoryId") Integer id, @Param("categoryName") String categoryName);


}