package com.example.backend.services;

import com.example.backend.dtos.EventAddDTO;
import com.example.backend.dtos.EventAllDTO;
import com.example.backend.dtos.EventDTO;
import com.example.backend.entities.Event;
import com.example.backend.entities.EventCategory;
import com.example.backend.repositories.EventCategoryRepository;
import com.example.backend.repositories.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventCategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper = ListMapper.getInstance();

    @Autowired
    public EventService(EventRepository repository, ModelMapper modelMapper){
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public List<EventAllDTO> getEventAllDTO(){
        List<Event> events = repository.findAll(Sort.by("eventStartTime").descending());
        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
    }

     public EventDTO getEventDTOById(int eventId){
        Event event = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,eventId + "is not existed."));
        return modelMapper.map(event, EventDTO.class);
     }

     public Event createEvent(EventAddDTO newEvent){
        int categoryId = newEvent.getEventCategoryId();
        EventCategory eventCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, categoryId + "is not existed."));
        Event event = new Event();
        event.setId(newEvent.getId());
        event.setBookingName(newEvent.getBookingName());
        event.setBookingEmail(newEvent.getBookingEmail());
        event.setEventStartTime(newEvent.getStartTime());
        event.setEventDuration(eventCategory.getEventCategoryDuration());
        event.setEventNotes(newEvent.getNotes());
        event.setEventCategory(eventCategory);
        return repository.saveAndFlush(event);
     }

    public void deleteEvent(int eventId){
        repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, eventId + " not found"));
        repository.deleteById(eventId);
    }

     public List<EventAllDTO> getEventAllDTOByCategory(int id){
        List<Event> events = repository.findByEventCategoryIdOrderByEventStartTimeDesc(id);
        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
     }
     
     public List<EventAllDTO> getPastEventAllDTO(){
        Date currentDate = new Date(System.currentTimeMillis());
        List<Event> events = repository.findByEventStartTimeLessThanOrderByEventStartTimeDesc(currentDate);
        return listMapper.mapList(events, EventAllDTO.class,modelMapper);
     }

     public List<EventAllDTO> getUpcomingEventAllDTO(){
         Date currentDate = new Date(System.currentTimeMillis());
         List<Event> events = repository.findByEventStartTimeGreaterThanOrderByEventStartTimeAsc(currentDate);
        return listMapper.mapList(events, EventAllDTO.class,modelMapper);
     }


    public List<EventAllDTO> getEventALLDTOByDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Thailand"));
        Date date = sdf.parse(dateString);
        Date dateEnd = new Date(date.getTime() + (1000 * 60 * 60 * 24));

        List<Event> events = repository.findByEventStartTimeGreaterThanAndEventStartTimeLessThan(date, dateEnd);

        return listMapper.mapList(events,EventAllDTO.class,modelMapper);
    }

}
