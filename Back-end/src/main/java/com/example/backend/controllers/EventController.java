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
@CrossOrigin(origins = "*")
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
    public List<EventAllDTO> getEventDTOByCategory(@Valid @PathVariable int categoryId) {
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
    public int createEvent(@Valid @RequestBody EventAddDTO newEvent, HttpServletRequest request, HttpServletResponse response) throws ParseException {
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