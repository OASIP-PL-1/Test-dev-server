package com.example.backend.controllers;

import com.example.backend.dtos.*;
import com.example.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
// @CrossOrigin(origins = "*")
public class EventController {
    @Autowired
    private EventService service;

    @GetMapping("")
    public List<EventAllDTO> getAllEventDTO(HttpServletRequest request, HttpServletResponse response) {
        return service.getEventAllDTO(request, response);
    }

    @GetMapping("/{eventId}")
    public EventDTO getEventDTOById(@Valid @PathVariable int eventId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return service.getEventDTOById(eventId, request, response);
    }

    @GetMapping("/category/{categoryId}")
    public List<EventAllDTO> getEventDTOByCategory(@Valid @PathVariable int categoryId, HttpServletRequest request, HttpServletResponse response) {
        return service.getEventAllDTOByCategory(categoryId, request, response);
    }

    @GetMapping("/past")
    public List<EventAllDTO> getPastEvent(HttpServletRequest request, HttpServletResponse response) {
        return service.getPastEventAllDTO(request, response);
    }

    @GetMapping("/upcoming")
    public List<EventAllDTO> getUpcomingEvent(HttpServletRequest request, HttpServletResponse response) {
        return service.getUpcomingEventAllDTO(request, response);
    }

    @GetMapping("/date/{date}")
    public List<EventAllDTO> getEventByDate(@PathVariable String date, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        return service.getEventALLDTOByDate(date, request, response);
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
        return service.getEventOverlapList(categoryId, dateTime);
    }

    @GetMapping("/list-edit-overlap/{eventId}/{dateTime}")
    public List<EventListOverlapDTO> lisEditOverlap(@PathVariable int eventId, @PathVariable String dateTime) throws ParseException {
        return service.listEditOverlap(eventId, dateTime);
    }


    @PostMapping("")
    public int createEvent(@Valid @RequestBody EventAddDTO newEvent,
                           HttpServletRequest request, HttpServletResponse response) throws ParseException {
        return service.createEvent(newEvent, request, response);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable int eventId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.deleteEvent(eventId, request, response);
    }

    @PutMapping("")
    public EventDTO editEvent(@Valid @RequestBody EventUpdateDTO updateEvent, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {
        return service.editEvent(updateEvent, request, response);
    }

}