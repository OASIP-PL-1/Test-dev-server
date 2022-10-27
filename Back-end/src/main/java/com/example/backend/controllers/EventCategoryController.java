package com.example.backend.controllers;

import com.example.backend.dtos.EventCategoryAllDTO;
import com.example.backend.dtos.EventCategoryNameDTO;
import com.example.backend.services.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/eventcategories")
// @CrossOrigin(origins = "*")
public class EventCategoryController {
    @Autowired
    EventCategoryService service;

    @GetMapping("/name")
    public List<EventCategoryNameDTO> getAllEventCategoryName(){
        return service.getEventCategoryNameDTO();
    }

    @GetMapping("")
    public List<EventCategoryAllDTO> getAllEventCategory(){
        return service.getEventCategoryAllDTO();
    }

    @PutMapping("")
    public EventCategoryAllDTO editEventCategory(@Valid @RequestBody EventCategoryAllDTO editCategory){
        return service.editCategory(editCategory);
    }

}
