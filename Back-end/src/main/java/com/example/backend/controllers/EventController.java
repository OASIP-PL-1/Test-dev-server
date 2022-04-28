package com.example.backend.controllers;

import com.example.backend.dtos.EventDTO;
import com.example.backend.entities.Event;
import com.example.backend.repositories.EventRepository;
import com.example.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController{
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventService service;

    @GetMapping("")
    public List<EventDTO> getAllEventDTO(){
        return service.getEventDTO();
    }

    @GetMapping("/{id}")
    public EventDTO getEventDTOById(@PathVariable int id){
        return service.getEventDTOById(id);
    }
}