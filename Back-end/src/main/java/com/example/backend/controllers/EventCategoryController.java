package com.example.backend.controllers;

import com.example.backend.dtos.EventCategoryAllDTO;
import com.example.backend.entities.EventCategory;
import com.example.backend.repositories.EventCategoryRepository;
import com.example.backend.services.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/eventcategories")
@CrossOrigin(origins = "*")
public class EventCategoryController {
    @Autowired
    EventCategoryService service;
    @Autowired
    EventCategoryRepository repository;

    @GetMapping("")
    public List<EventCategoryAllDTO> getAllEventCategory(){
        return service.getEventCategoryAllDTO();
    }
//
//    @GetMapping("/{id}")
//    public EventCategory getE(@PathVariable int id){
//        return repository.findById(id).orElseThrow(
//
//        );
//    }
}
