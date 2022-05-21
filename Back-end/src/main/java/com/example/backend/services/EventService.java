package com.example.backend.services;

import com.example.backend.dtos.*;
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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventCategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    public EventService(EventRepository repository, ModelMapper modelMapper){
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    //GET method
    public List<EventAllDTO> getEventAllDTO(){
        List<Event> events = repository.findAll(Sort.by("eventStartTime").descending());
        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
    }

     public EventDTO getEventDTOById(int eventId){
        Event event = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Choosen event is not existed."));
        return modelMapper.map(event, EventDTO.class);
     }

     public List<EventListOverlapDTO> listEditOverlap(int eventId, String dateTime) throws ParseException {
        int categoryId = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"This event is not existed.")).getEventCategory().getId();
        return getEventOverlapList(categoryId, dateTime);
     }

    public List<EventListOverlapDTO> getEventOverlapList(int categoryId, String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);
        Date dateEnd = new Date(date.getTime() + (1000 * 60 * 60 * 24));
        System.out.println("getEventOverlapList : " + categoryId + "---" + date + "---" + dateEnd);
        EventCategory eventCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"CategoryId is not existed."));
        List<Event> events = repository.findByEventCategoryIdAndEventStartTimeBetweenOrderByEventStartTimeAsc(categoryId,date, dateEnd);
//        List<Event> events = repository.findByEventStartTimeGreaterThanAndEventStartTimeLessThan(date, dateEnd);
        return listMapper.mapList(events, EventListOverlapDTO.class,modelMapper);
    }

     //GET method - filter
    public List<EventAllDTO> getEventAllDTOByCategory(int id){
        List<Event> events = repository.findByEventCategoryIdOrderByEventStartTimeDesc(id);
        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
    }

    public List<EventAllDTO> getPastEventAllDTO(){
        Date currentDate = new Date(System.currentTimeMillis());
        List<Event> events = repository.filterPastEvent(currentDate);
        return listMapper.mapList(events, EventAllDTO.class,modelMapper);
    }

    public List<EventAllDTO> getUpcomingEventAllDTO(){
        Date currentDate = new Date(System.currentTimeMillis());
        List<Event> events = repository.filterUpcomingEvent(currentDate);
        return listMapper.mapList(events, EventAllDTO.class,modelMapper);
    }

    public List<EventAllDTO> getEventALLDTOByDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);    
        Date dateEnd = new Date(date.getTime() + (1000 * 60 * 60 * 24));
        System.out.println("Filter Date :" + date+ "---" +dateEnd);
        List<Event> events = repository.findByEventStartTimeGreaterThanAndEventStartTimeLessThan(date, dateEnd);
        return listMapper.mapList(events,EventAllDTO.class,modelMapper);
    }


    //POST method
     public int createEvent(EventAddDTO newEvent){
        if(newEvent==null){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The data wasn't fulfilled");}
        if(newEvent.getBookingName()==null){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The booking name is required.");}
        if(newEvent.getBookingEmail()==null){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The booking emil is required.");}
        if(newEvent.getEventCategoryId()==0){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The category must be choosen.");}
        if(newEvent.getStartTime()==null){ throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date must be chosen.");}
        if(newEvent.getBookingName().length() > 100){ throw new ResponseStatusException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, "Booking name is too long. Maximum length is 100."); }
        if(newEvent.getBookingEmail().length() > 50){ throw new ResponseStatusException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, "Booking email is too long. Maximum length is 50."); }
        if(!checkEmail(newEvent.getBookingEmail())){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is not valid");}
        if(checkStartDate(newEvent.getStartTime())){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The booking date is in the past.");}
        if(newEvent.getNotes() != null) { if (newEvent.getNotes().length() > 500) { throw new ResponseStatusException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, "Notes is too long. Maximum length is 500.");} }
         System.out.println("New Event getStartTime :" + newEvent.getStartTime());
        if(!(checkOverlap(newEvent.getEventCategoryId(), newEvent.getStartTime()).size()==0)){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The choosen time is overlap other events");}
        EventCategory eventCategory = categoryRepository.findById(newEvent.getEventCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This category is not existed."));
        Event event = new Event();
        event.setId(newEvent.getId());
        event.setBookingName(newEvent.getBookingName());
        event.setBookingEmail(newEvent.getBookingEmail());
        event.setEventStartTime(newEvent.getStartTime());
        event.setEventDuration(eventCategory.getEventCategoryDuration());
        event.setEventNotes(newEvent.getNotes());
        event.setEventCategory(eventCategory);
        repository.saveAndFlush(event);
        return repository.findTopByOrderByIdDesc().getId();
     }

     //DELETE method
    public void deleteEvent(int eventId){
        repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choosen event is not existed"));
        repository.deleteById(eventId);
    }

    //PUT method
    public EventDTO editEvent(EventUpdateDTO updateEvent){
        if(updateEvent==null){ throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"The data wasn't fulfilled.");}
        if(updateEvent.getStartTime()==null){ throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date must be chosen.");}
        if(checkStartDate(updateEvent.getStartTime())){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The booking date is in the past.");}
        if(updateEvent.getNotes() != null) { if (updateEvent.getNotes().length() > 500) { throw new ResponseStatusException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, "Notes is too long. Maximum length is 500.");} }
        Event event = repository.findById(updateEvent.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " event not found.") );
        System.out.println("Update Event getStartTime :" + updateEvent.getStartTime());
        if(!checkEditOverlap(event,updateEvent.getStartTime())){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The event is overlap another events.");}
        event.setEventStartTime(updateEvent.getStartTime());
        event.setEventNotes(updateEvent.getNotes());
        repository.saveAndFlush(event);
        return modelMapper.map(event,EventDTO.class);
    }


    //Checkoverlap part------------------

    public boolean checkBookOverlapForFrontEnd(int categoryId, String dateTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/ฺBangkok"));
        Date startTime = sdf.parse(dateTime);
        System.out.println("check BookOverlap StartTime : "+ startTime);
        return checkOverlap(categoryId, startTime).size() == 0;
    }

    public boolean checkEditOverlapForFrontEnd(int eventId, String dateTime) throws ParseException {
        Event event = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The choosen event is not existed."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/ฺBangkok"));
        Date editTime = sdf.parse(dateTime);
        System.out.println("check EditOverlap StartTime : "+ editTime);
        return checkEditOverlap(event, editTime);
    }

   public List<Event> checkOverlap(int categoryId, Date date) {
       EventCategory eventCategory = categoryRepository.findById(categoryId).orElseThrow(() -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category is not exist"); });
       System.out.println(eventCategory.getEventCategoryDuration());
       Date startTime = date;
       Date endTime = new Date(startTime.getTime() + 1000*60*eventCategory.getEventCategoryDuration());
       System.out.println(startTime+ "*********" +endTime);
       List<Event> event = repository.overlap(categoryId,startTime,endTime);
       return event;
    };

    //Private method for validation
    private boolean checkEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    private boolean checkStartDate(Date startDate){
        Date currentDate = new Date();
        return startDate.before(currentDate);
    }

    private boolean checkEditOverlap(Event event, Date editTime){
        int eventSize = checkOverlap(event.getEventCategory().getId(), editTime).size();
        //check self overlap
        int minutesBetweenDate = (int) TimeUnit.MINUTES.convert((Math.abs(editTime.getTime() - event.getEventStartTime().getTime())), TimeUnit.MILLISECONDS);
        System.out.println("Minute between date : " +  minutesBetweenDate);
        if(minutesBetweenDate < event.getEventDuration()){
            eventSize--;
        }
        return eventSize == 0;
    }
}

