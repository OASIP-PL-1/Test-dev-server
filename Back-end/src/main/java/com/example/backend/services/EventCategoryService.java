package com.example.backend.services;

import com.example.backend.dtos.EventAllDTO;
import com.example.backend.dtos.EventCategoryAllDTO;
import com.example.backend.dtos.EventCategoryNameDTO;
import com.example.backend.entities.EventCategory;
import com.example.backend.repositories.EventCategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventCategoryService {
    @Autowired
    EventCategoryRepository repository;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

    public List<EventCategoryNameDTO> getEventCategoryNameDTO() {
        List<EventCategory> eventCategories = repository.findAll();
        return listMapper.mapList(eventCategories, EventCategoryNameDTO.class, modelMapper);
    }

    public List<EventCategoryAllDTO> getEventCategoryAllDTO(){
        List<EventCategory> eventCategories = repository.findAll();
        return listMapper.mapList(eventCategories, EventCategoryAllDTO.class, modelMapper);
    }

    public EventCategoryAllDTO editCategory(EventCategoryAllDTO editCategory){
        if(editCategory.getId() == 0){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"categoryId is required");}
        if(editCategory.getCategoryName() == null){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"categoryName is required.");}
        if(editCategory.getDuration() < 1 || editCategory.getDuration() > 480){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Event category duration out of length.");}
        editCategory.setCategoryName(editCategory.getCategoryName().trim());
        if(editCategory.getCategoryDescription() != null){
            editCategory.setCategoryDescription(editCategory.getCategoryDescription().trim());
            if(editCategory.getCategoryDescription().length() > 500){ throw new ResponseStatusException(HttpStatus.URI_TOO_LONG,"category description is too long, maximum length is 500 characters.");}
        }
        if(editCategory.getCategoryName().length() > 100){ throw new ResponseStatusException(HttpStatus.URI_TOO_LONG,"category name is too long, maximum length is 100 characters");}
        if(repository.checkExistedName(editCategory.getId(), editCategory.getCategoryName()) > 0){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Event category name is already existed, please choose another name.");}
        EventCategory editedCategory= modelMapper.map(editCategory, EventCategory.class);
        repository.saveAndFlush(editedCategory);
        return modelMapper.map(editedCategory, EventCategoryAllDTO.class);
    }

}
