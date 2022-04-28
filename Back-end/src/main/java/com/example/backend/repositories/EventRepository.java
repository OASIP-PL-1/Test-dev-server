package com.example.backend.repositories;

import com.example.backend.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository <Event, Integer> {
}
