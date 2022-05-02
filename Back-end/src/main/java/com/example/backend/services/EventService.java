package com.example.backend.services;

import com.example.backend.dtos.EventAllDTO;
import com.example.backend.dtos.EventDTO;
import com.example.backend.entities.Event;
import com.example.backend.repositories.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    public EventService(EventRepository repository, ModelMapper modelMapper){
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public List<EventAllDTO> getEventDTO(){
        List<Event> events = repository.findAll(Sort.by("eventStartTime").descending());
        return listMapper.mapList(events, EventAllDTO.class,modelMapper);
    }

     public EventDTO getEventDTOById(int eventId){
        Event event = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,eventId + "is not existed."));
        return convertEntityToDTO(event);
     }

     private EventDTO convertEntityToDTO(Event event){
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setBookingName(event.getBookingName());
        eventDTO.setBookingEmail(event.getBookingEmail());
        eventDTO.setCategoryName(event.getEventCategory().getEventCategoryName());
        eventDTO.setDuration(event.getEventDuration());
        eventDTO.setNotes(event.getEventNotes());
        eventDTO.setStartTime(event.getEventStartTime());
        return eventDTO;
     }
}
