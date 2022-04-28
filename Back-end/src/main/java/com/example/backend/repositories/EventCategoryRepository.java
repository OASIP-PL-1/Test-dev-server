package com.example.backend.repositories;

import com.example.backend.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
}