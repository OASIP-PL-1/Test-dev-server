package com.example.backend.services;

import com.auth0.jwt.JWT;
import com.example.backend.dtos.*;
import com.example.backend.entities.Event;
import com.example.backend.entities.EventCategory;
import com.example.backend.entities.User;
import com.example.backend.repositories.EventCategoryOwnerRepository;
import com.example.backend.repositories.EventCategoryRepository;
import com.example.backend.repositories.EventRepository;
import com.example.backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventCategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventCategoryOwnerRepository categoryOwnerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper = ListMapper.getInstance();
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private FileService fileService;

    @Autowired
    public EventService(EventRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    //GET method
    public List<EventAllDTO> getEventAllDTO(HttpServletRequest request, HttpServletResponse response) {
        String role = new Authorization().getRoleFromRequest(request);
        if(role.equals("admin")) {
            List<Event> events = repository.findAll(Sort.by("eventStartTime").descending());
            return listMapper.mapList(events, EventAllDTO.class, modelMapper);
        } else if(role.equals("student")) {
            List<Event> events = repository.findByBookingEmail(new Authorization().getUserEmailFromRequest(request));
            return listMapper.mapList(events, EventAllDTO.class, modelMapper);
        } else if(role.equals("lecturer")) {
            List<Event> events = repository.lecturerGetEvent(new Authorization().getUserEmailFromRequest(request));
            return listMapper.mapList(events, EventAllDTO.class, modelMapper);
        }
        return null;
    }

    public EventDTO getEventDTOById(int eventId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Event event = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choosen event is not existed."));
        String role = new Authorization().getRoleFromRequest(request);
        String userEmail = new Authorization().getUserEmailFromRequest(request);
        if(role.equals("admin")) {
            return modelMapper.map(event, EventDTO.class);
        }
        if(role.equals("student")){
            if(event.getBookingEmail().equals(userEmail)) return modelMapper.map(event, EventDTO.class);
        }
        if(role.equals("lecturer")){
            User user = userRepository.findByUserEmail(userEmail);
            if(repository.checkLecturerCategory(eventId, user.getId())==1){
                return modelMapper.map(event, EventDTO.class);
            }
        }
        response.setStatus(403);
        response.getWriter().print("Unauthorized.");
        return null;
    }

    public List<EventListOverlapDTO> listEditOverlap(int eventId, String dateTime) throws ParseException {
        int categoryId = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This event is not existed.")).getEventCategory().getId();
        return getEventOverlapList(categoryId, dateTime);
    }

    public List<EventListOverlapDTO> getEventOverlapList(int categoryId, String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);
        Date dateEnd = new Date(date.getTime() + (1000 * 60 * 60 * 24));
        EventCategory eventCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CategoryId is not existed."));
        List<Event> events = repository.findByEventCategoryIdAndEventStartTimeBetweenOrderByEventStartTimeAsc(categoryId, date, dateEnd);
        return listMapper.mapList(events, EventListOverlapDTO.class, modelMapper);
    }

    //GET method - filter
    public List<EventAllDTO> getEventAllDTOByCategory(int id, HttpServletRequest request, HttpServletResponse response) {
        List<Event> events = new ArrayList<>();
        String role = new Authorization().getRoleFromRequest(request);
        if(role.equals("admin")) {
            events = repository.findByEventCategoryIdOrderByEventStartTimeDesc(id);
        } else if(role.equals("student")){
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            events = repository.findByEventCategoryIdAndAndBookingEmailOrderByEventStartTime(id, userEmail);
        } else {
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            int lecturerId = userRepository.findByUserEmail(userEmail).getId();
//            System.out.println(lecturerId);
//            System.out.println(categoryOwnerRepository.findByUserIdAndEventCategoryId(lecturerId,id));
            if(categoryOwnerRepository.findByUserIdAndEventCategoryId(lecturerId,id)!=null){
                events = repository.findByEventCategoryIdOrderByEventStartTimeDesc(id);
            }
//            events = repository.lecturerGetEventByCategoryId(id, userRepository.findByUserEmail(userEmail).getId());
        }
        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
    }

    public List<EventAllDTO> getPastEventAllDTO(HttpServletRequest request, HttpServletResponse response) {
        Date currentDate = new Date(System.currentTimeMillis());
        List<Event> events;
        String role = new Authorization().getRoleFromRequest(request);
        if(role.equals("admin")){
            events = repository.filterPastEvent(currentDate);
        } else if (role.equals("student")) {
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            events = repository.studentFilterPastEvent(currentDate, userEmail);
        } else { //lecturer
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            int lecturerId = userRepository.findByUserEmail(userEmail).getId();
            events = repository.lecturerFilterPastEvent(currentDate, lecturerId);
        }

        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
    }

    public List<EventAllDTO> getUpcomingEventAllDTO(HttpServletRequest request, HttpServletResponse response) {
        Date currentDate = new Date(System.currentTimeMillis());
        List<Event> events;
        String role = new Authorization().getRoleFromRequest(request);
        if(role.equals("admin")){
            events = repository.filterUpcomingEvent(currentDate);
        } else if (role.equals("student")) {
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            events = repository.studentFilterUpcomingEvent(currentDate, userEmail);
        } else { //lecturer
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            int lecturerId = userRepository.findByUserEmail(userEmail).getId();
            events = repository.lecturerFilterUpcomingEvent(currentDate, lecturerId);
        }
        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
    }

    public List<EventAllDTO> getEventALLDTOByDate(String dateString, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        Date date = sdf.parse(dateString);
        Date dateEnd = new Date(date.getTime() + (1000 * 60 * 60 * 24));
        System.out.println(date + "and" + dateEnd);
        List<Event> events;
        String role = new Authorization().getRoleFromRequest(request);
        if(role.equals("admin")){
            events = repository.findByEventStartTimeBetweenOrderByEventStartTimeAsc(date, dateEnd);
        } else if (role.equals("student")){
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            events = repository.findByEventStartTimeBetweenAndBookingEmailOrderByEventStartTimeAsc(date, dateEnd, userEmail);
        } else { //lecturer
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            int lecturerId = userRepository.findByUserEmail(userEmail).getId();
            events = repository.lecturerFilterByDate(date, dateEnd, lecturerId);
        }
        return listMapper.mapList(events, EventAllDTO.class, modelMapper);
    }

    public int createEvent(EventAddDTO newEvent, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        if(request.getHeader("Authorization") != null){
            String role = new Authorization().getRoleFromRequest(request);
            if (role.equals("student")) {
                System.out.println(newEvent.getBookingEmail());
                System.out.println(new Authorization().getUserEmailFromRequest(request).equals(newEvent.getBookingEmail()));
                if (!new Authorization().getUserEmailFromRequest(request).equals(newEvent.getBookingEmail()))
                    if(newEvent.getEventAttachmentName()!=null) fileService.deleteFile(newEvent.getEventAttachmentName());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The booking email didn't match your email.");
            }
        }
        if (!(checkOverlap(newEvent.getEventCategoryId(), newEvent.getStartTime()).size() == 0)) {
            if(newEvent.getEventAttachmentName()!=null) fileService.deleteFile(newEvent.getEventAttachmentName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The choosen time is overlap other events");
        }
        EventCategory eventCategory = categoryRepository.findById(newEvent.getEventCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This category is not existed."));
        Event event = new Event();
        event.setId(newEvent.getId());
        event.setBookingName(newEvent.getBookingName());
        event.setBookingEmail(newEvent.getBookingEmail());
        event.setEventStartTime(newEvent.getStartTime());
        event.setEventDuration(eventCategory.getEventCategoryDuration());
        event.setEventNotes(newEvent.getNotes());
        event.setEventCategory(eventCategory);
        event.setEventAttachmentName(newEvent.getEventAttachmentName());
        System.out.println(event.getBookingEmail());
        repository.saveAndFlush(event);
        emailService.sendSimpleMailWhenCreateEvent(event);
        return repository.findTopByOrderByIdDesc().getId();
    }

    //DELETE method
    public void deleteEvent(int eventId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String role = new Authorization().getRoleFromRequest(request);
        Event event = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choosen event is not existed"));
        if(role.equals("admin")) {
            if(event.getEventAttachmentName() != null) fileService.deleteFile(event.getEventAttachmentName());
            repository.deleteById(eventId);
            return;
        }
        if(role.equals("student")){
            String userEmail = new Authorization().getUserEmailFromRequest(request);
            if(event.getBookingEmail().equals(userEmail)) {
                if(event.getEventAttachmentName() != null) fileService.deleteFile(event.getEventAttachmentName());
                repository.deleteById(eventId);
                return;
            }
        }
        response.setStatus(403);
        response.getWriter().print("UnAuthorized");
    }

    //PUT method
    public EventDTO editEvent(EventUpdateDTO updateEvent, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {
        Event event = repository.findById(updateEvent.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " event not found."));
        String role = new Authorization().getRoleFromRequest(request);
        if(role.equals("admin") || role.equals("student")){
            if(role.equals("student")){
                String userEmail = new Authorization().getUserEmailFromRequest(request);
                if(!event.getBookingEmail().equals(userEmail)) {
                    if(updateEvent.getEventAttachmentName()!=null) fileService.deleteFile(updateEvent.getEventAttachmentName());
                    response.setStatus(403);
                    response.getWriter().print("Unauthorized.");
                    return null;
                }
            }
            if (!checkEditOverlap(event, updateEvent.getStartTime())) {
                if(updateEvent.getEventAttachmentName()!=null) fileService.deleteFile(updateEvent.getEventAttachmentName());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The event is overlap another events.");
            }
            event.setEventStartTime(updateEvent.getStartTime());
            event.setEventNotes(updateEvent.getNotes());
            if(updateEvent.getEventAttachmentName()==null){
                if(event.getEventAttachmentName()!=null){
                    fileService.deleteFile(event.getEventAttachmentName());
                    event.setEventAttachmentName(null);
                }
            } else {
                if(event.getEventAttachmentName()!=null) {
                    if (!event.getEventAttachmentName().equals(updateEvent.getEventAttachmentName())) {
                        fileService.deleteFile(event.getEventAttachmentName());
                    }
                }
                event.setEventAttachmentName(updateEvent.getEventAttachmentName());
            }
            repository.saveAndFlush(event);
            return modelMapper.map(event, EventDTO.class);
        }
        if(updateEvent.getEventAttachmentName()!=null) fileService.deleteFile(updateEvent.getEventAttachmentName());
        response.setStatus(403);
        response.getWriter().print("Unauthorized.");
        return null;
    }

    //Checkoverlap part------------------

    public boolean checkBookOverlapForFrontEnd(int categoryId, String dateTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Thailand"));
        Date startTime = sdf.parse(dateTime);
        EventCategory eventCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category is not existed."));
        return checkOverlap(categoryId, startTime).size() == 0;
    }

    public boolean checkEditOverlapForFrontEnd(int eventId, String dateTime) throws ParseException {
        Event event = repository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The choosen event is not existed."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Thailand"));
        Date editTime = sdf.parse(dateTime);
        return checkEditOverlap(event, editTime);
    }

    private List<Event> checkOverlap(int categoryId, Date date) {
        EventCategory eventCategory = categoryRepository.findById(categoryId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category is not exist");
        });
        Date startTime = date;
        Date endTime = new Date(startTime.getTime() + 1000 * 60 * eventCategory.getEventCategoryDuration());
        List<Event> event = repository.overlap(categoryId, startTime, endTime);
        return event;
    }

    //Private method for validation
    private boolean checkEditOverlap(Event event, Date editTime) throws ParseException {
        Date startTime = editTime;
        Date endTime = new Date(startTime.getTime() + 1000 * 60 * event.getEventDuration());
        System.out.println(startTime + "*********" + endTime);
        int eventSize = repository.overlap(event.getEventCategory().getId(), startTime, endTime).size();
        //check self overlap
        int minutesBetweenDate = (int) TimeUnit.MINUTES.convert((Math.abs(editTime.getTime() - event.getEventStartTime().getTime())), TimeUnit.MILLISECONDS);
        System.out.println("Minute between date : " + minutesBetweenDate);
        if (minutesBetweenDate < event.getEventDuration()) {
            System.out.println("match last event");
            eventSize--;
        }
        System.out.println(eventSize);
        return eventSize == 0;
    }

}

