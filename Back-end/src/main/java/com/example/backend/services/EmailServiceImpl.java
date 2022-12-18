package com.example.backend.services;

import com.example.backend.entities.EmailDetails;
import com.example.backend.entities.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.DateFormatSymbols;
import java.util.Date;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendSimpleMail(EmailDetails details) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());
            javaMailSender.send(mailMessage);
            return "mail success";
        }
        catch (Exception e){
            return "mail failed";
        }
    }

    @Override
    public String sendMailWithAttachment(EmailDetails details) {
        return null;
    }

    public void sendSimpleMailWhenCreateEvent(Event event){
        EmailDetails e = new EmailDetails();
        e.setRecipient(event.getBookingEmail());
        String dayNames[] = new DateFormatSymbols().getWeekdays();
        String monthNames[] = new DateFormatSymbols().getShortMonths();
        Date date = event.getEventStartTime();
        Date finishedDate = new Date(date.getTime()+(event.getEventDuration()*60*1000));
        String dateDetailed = dayNames[date.getDay()+1].substring(0,3) + " " +
                monthNames[date.getMonth()] + " " +
                date.getDate() + ", " +
                (1900+ date.getYear()) + " " +
                (date.getHours()<10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes()<10 ? "0" + date.getMinutes() : date.getMinutes()) + " - " +
                (finishedDate.getHours()<10 ? "0" + finishedDate.getHours() : finishedDate.getHours()) + ":" + (finishedDate.getMinutes()<10 ? "0" + finishedDate.getMinutes() : finishedDate.getMinutes()) +
                " (ICT)";
        String subjectName = "[OASIP] Server-side Clinic @ " + dateDetailed;
        e.setSubject(subjectName);
        e.setMsgBody(subjectName + "\n" +
                "Reply-to: " + sender + "\n" +
                "Booking Name: " + event.getBookingName() + "\n" +
                "Event Category: " + event.getEventCategory().getEventCategoryName() + "\n" +
                "When: " + dateDetailed + "\n" +
                "Event Notes: " + (event.getEventNotes()==null ? "-" : event.getEventNotes()));
        sendSimpleMail(e);
    }
}
