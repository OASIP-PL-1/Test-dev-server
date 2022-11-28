package com.example.backend.services;

import com.example.backend.entities.EmailDetails;

public interface EmailService{
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
}
