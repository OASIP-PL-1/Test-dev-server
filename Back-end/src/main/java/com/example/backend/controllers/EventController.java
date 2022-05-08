package com.example.backend.controllers;

import com.example.backend.dtos.EventAddDTO;
import com.example.backend.dtos.EventAllDTO;
import com.example.backend.dtos.EventDTO;
import com.example.backend.entities.Event;
import com.example.backend.repositories.EventRepository;
import com.example.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventService service;

    @GetMapping("")
    public List<EventAllDTO> getAllEventDTO() {
        return service.getEventAllDTO();
    }

    @GetMapping("/{id}")
    public EventDTO getEventDTOById(@PathVariable int id) {
        return service.getEventDTOById(id);
    }

    @PostMapping("")
    public Event create(@RequestBody EventAddDTO newEvent) {
        return service.createEvent(newEvent);
    }

    @DeleteMapping("/{eventId}")
    public void delete(@PathVariable int eventId) {
        service.deleteEvent(eventId);
    }

    @GetMapping("/category/{id}")
    public List<EventAllDTO> getEventDTOByCategory(@PathVariable int id) {
        return service.getEventAllDTOByCategory(id);
    }

    @GetMapping("/past")
    public List<EventAllDTO> getPastEvent() {
        return service.getPastEventAllDTO();
    }

    @GetMapping("/upcoming")
    public List<EventAllDTO> getUpcomingEvent() {
        return service.getUpcomingEventAllDTO();
    }

    @GetMapping("/date/{date}")
    public List<EventAllDTO> getEventByDate(@PathVariable String date) throws ParseException {
        return service.getEventALLDTOByDate(date);
    }

}