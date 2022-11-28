package com.example.backend.services;

import com.example.backend.entities.EmailDetails;
import com.example.backend.entities.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
            System.out.println(sender);
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());
            javaMailSender.send(mailMessage);
            return "mail success";
        }
        catch (Exception e){
            System.out.println(e);
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
        e.setSubject("no-reply : Event created");
        System.out.println();
        e.setMsgBody("Your event is successfully created. Please take time for your queue until the booking Date.\n\n"+
                "Event detail\n"+
                "Booking name : " + event.getBookingName() + "\n" +
                "Booking time : " + event.getEventStartTime() + "-" + new Date(event.getEventStartTime().getTime()+ event.getEventDuration()*60*1000) + "\n" +
                "Booking category : " + event.getEventCategory().getEventCategoryName() + "\n" +
                "Booking notes : " + event.getEventNotes() + "\n" +
                "Event attachment's name : " + event.getEventAttachmentName() + "\n\n" +
                "Please don't reply this email, no one will reply back.");
        System.out.println(sendSimpleMail(e));
    }
}
