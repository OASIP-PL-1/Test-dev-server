package com.example.backend;

import com.example.backend.repositories.EventRepository;
import com.example.backend.services.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;

@SpringBootApplication
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);


//        Date date = new Date(System.currentTimeMillis());
//        Instant instant = date.toInstant();
//        System.out.println(date);
//        System.out.println(instant);
//
//        System.out.println("------------------------");
//
//        Date date2 = new Date();
//        Instant instant2 = date2.toInstant();
//        System.out.println(date2);
//        System.out.println(instant2);
    }

}
