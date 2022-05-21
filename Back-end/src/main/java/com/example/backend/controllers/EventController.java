package com.example.backend.controllers;

import com.example.backend.dtos.EventAddDTO;
import com.example.backend.dtos.EventAllDTO;
import com.example.backend.dtos.EventDTO;
import com.example.backend.dtos.EventUpdateDTO;
import com.example.backend.entities.Event;
import com.example.backend.entities.EventCategory;
import com.example.backend.repositories.EventCategoryRepository;
import com.example.backend.repositories.EventRepository;
import com.example.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    @Autowired
    private EventService service;

    @GetMapping("")
    public List<EventAllDTO> getAllEventDTO() {
        return service.getEventAllDTO();
    }

    @GetMapping("/{eventId}")
    public EventDTO getEventDTOById(@PathVariable int eventId) {
        return service.getEventDTOById(eventId);
    }


    @GetMapping("/category/{categoryId}")
    public List<EventAllDTO> getEventDTOByCategory(@PathVariable int categoryId) {
        return service.getEventAllDTOByCategory(categoryId);
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

    @GetMapping("/book/{categoryId}/{dateTime}")
    public boolean checkBookOverlap(@PathVariable int categoryId, @PathVariable String dateTime) throws ParseException {
        return service.checkBookOverlapForFrontEnd(categoryId, dateTime);
    }

    @GetMapping("/edit/{eventId}/{dateTime}")
    public boolean checkEditOverlap(@PathVariable int eventId, @PathVariable String dateTime) throws ParseException {
        return service.checkEditOverlapForFrontEnd(eventId, dateTime);
    }

    @PostMapping("")
    public int createEvent(@RequestBody EventAddDTO newEvent) {
        return service.createEvent(newEvent);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable int eventId) {
        service.deleteEvent(eventId);
    }

    @PutMapping("")
    public EventDTO editEvent(@RequestBody EventUpdateDTO updateEvent) {
        return service.editEvent(updateEvent);
    }


    @GetMapping("/test2/{categoryId}/{dateTime}")
    public List<Event> test2(@PathVariable int categoryId, @PathVariable String dateTime) throws ParseException {
        EventCategory eventCategory = eventCategoryRepository.findById(categoryId).orElseThrow();
        System.out.println(eventCategory.getEventCategoryDuration());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date startTime = formatter.parse(dateTime);
        Date endTime = new Date(startTime.getTime() + 1000*60*eventCategory.getEventCategoryDuration());
        System.out.println(startTime+ "*********" +endTime);
        List<Event> event = repository.overlap(categoryId,startTime,endTime);
        return event;
    }


}