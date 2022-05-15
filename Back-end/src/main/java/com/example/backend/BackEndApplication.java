package com.example.backend;

import com.example.backend.services.EventService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;


@SpringBootApplication
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);


//        EventService service;
//        Date starTime = new Date(2022-05-0403::00:00Z);
//        service.checkSelfOverlap()

//        Date date = new Date(System.currentTimeMillis());
//        Instant instant = date.toInstant();
//        System.out.println(date);
//        System.out.println(instant);
//
//        System.out.println("------------------------");
//


//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//        Date date = sdf.parse("2022-05-13-01-00-00");
//        Date date2 = new Date();
//        System.out.println(date);
//        System.out.println(date2);
//        long diff = Math.abs(date.getTime() - date2.getTime());
//        System.out.println(diff);
//        int minutes = (int) TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
//        System.out.println(minutes);



//        Instant instant2 = date2.toInstant();
//        System.out.println(date2);
//        System.out.println(instant2);
    }

}
