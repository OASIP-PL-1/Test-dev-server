package com.example.backend;

import com.example.backend.services.EventService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;


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

//        Date date = new Date();
//        System.out.println(date);
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        System.out.println(dateFormat.format(date));
//
//        Instant instant = date.toInstant();
//        System.out.println(instant);

    }

}
