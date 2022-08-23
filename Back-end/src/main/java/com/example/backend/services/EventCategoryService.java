package com.example.backend.services;

import com.example.backend.dtos.EventCategoryAllDTO;
import com.example.backend.dtos.EventCategoryNameDTO;
import com.example.backend.entities.EventCategory;
import com.example.backend.repositories.EventCategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class EventCategoryService {
    @Autowired
    EventCategoryRepository repository;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

    //GET method
    public List<EventCategoryNameDTO> getEventCategoryNameDTO() {
        List<EventCategory> eventCategories = repository.findAll();
        return listMapper.mapList(eventCategories, EventCategoryNameDTO.class, modelMapper);
    }

    public List<EventCategoryAllDTO> getEventCategoryAllDTO(){
        List<EventCategory> eventCategories = repository.findAll();
        return listMapper.mapList(eventCategories, EventCategoryAllDTO.class, modelMapper);
    }

    //PUT method
    public EventCategoryAllDTO editCategory(EventCategoryAllDTO editCategory){
//        if(repository.checkExistedName(editCategory.getId(), editCategory.getCategoryName()) > 0){ throw new ErrorDetails(new Date(),HttpStatus.BAD_REQUEST,new HashMap<String,String>());}
//        if(repository.checkExistedName(editCategory.getId(), editCategory.getCategoryName()) > 0){ throw new EventException(HttpStatus.BAD_REQUEST);}
//        if(repository.checkExistedName(editCategory.getId(), editCategory.getCategoryName()) > 0){ throw new EventException("NO");}

//        if(repository.checkExistedName(editCategory.getId(), editCategory.getCategoryName()) > 0){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Event category name is already existed, please choose another name.");}
        EventCategory editedCategory= modelMapper.map(editCategory, EventCategory.class);
        repository.saveAndFlush(editedCategory);
        return modelMapper.map(editedCategory, EventCategoryAllDTO.class);
    }

}
