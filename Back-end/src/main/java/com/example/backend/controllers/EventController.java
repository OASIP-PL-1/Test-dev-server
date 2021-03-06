package com.example.backend.controllers;

import com.example.backend.dtos.*;
import com.example.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
// @CrossOrigin(origins = "*")
public class EventController {
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

    @GetMapping("/list-book-overlap/{categoryId}/{dateTime}")
    public List<EventListOverlapDTO> listBookOverlap(@PathVariable int categoryId, @PathVariable String dateTime) throws ParseException {
        System.out.println(categoryId + "-----" + dateTime);
        return service.getEventOverlapList(categoryId, dateTime);
    }

    @GetMapping("/list-edit-overlap/{eventId}/{dateTime}")
    public List<EventListOverlapDTO> lisEditOverlap(@PathVariable int eventId, @PathVariable String dateTime) throws ParseException {
        return service.listEditOverlap(eventId, dateTime);
    }

    @PostMapping("")
    public int createEvent(@RequestBody EventAddDTO newEvent) throws ParseException {
        return service.createEvent(newEvent);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable int eventId) {
        service.deleteEvent(eventId);
    }

    @PutMapping("")
    public EventDTO editEvent(@RequestBody EventUpdateDTO updateEvent) throws ParseException {
        return service.editEvent(updateEvent);
    }




}