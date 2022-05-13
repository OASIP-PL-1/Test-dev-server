package com.example.backend.services;

import com.example.backend.dtos.EventAllDTO;
import com.example.backend.dtos.EventCategoryAllDTO;
import com.example.backend.entities.Event;
import com.example.backend.entities.EventCategory;
import com.example.backend.repositories.EventCategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCategoryService {
    @Autowired
    EventCategoryRepository repository;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

    public List<EventCategoryAllDTO> getEventCategoryAllDTO() {
        List<EventCategory> eventCategories = repository.findAll();
        return listMapper.mapList(eventCategories, EventCategoryAllDTO.class, modelMapper);
    }

//    public List<EventAllDTO> getEventAllDTO(){
//        List<Event> events = repository.findAll(Sort.by("eventStartTime").descending());
//        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
//    }

}
