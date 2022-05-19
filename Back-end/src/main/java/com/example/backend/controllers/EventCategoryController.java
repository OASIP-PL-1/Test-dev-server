package com.example.backend.controllers;

import com.example.backend.dtos.EventCategoryAllDTO;
import com.example.backend.dtos.EventCategoryNameDTO;
import com.example.backend.repositories.EventCategoryRepository;
import com.example.backend.services.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/eventcategories")
// @CrossOrigin(origins = "*")
public class EventCategoryController {
    @Autowired
    EventCategoryService service;
    @Autowired
    EventCategoryRepository repository;

    @GetMapping("/name")
    public List<EventCategoryNameDTO> getAllEventCategoryName(){
        return service.getEventCategoryNameDTO();
    }

    @GetMapping("")
    public List<EventCategoryAllDTO> getAllEventCategory(){
        return service.getEventCategoryAllDTO();
    }

    @PutMapping("")
    public EventCategoryAllDTO editEventCategory(@RequestBody EventCategoryAllDTO editCategory){
        return service.editCategory(editCategory);
    }

//
//    @GetMapping("/{id}")
//    public EventCategory getE(@PathVariable int id){
//        return repository.findById(id).orElseThrow(
//
//        );
//    }
}
