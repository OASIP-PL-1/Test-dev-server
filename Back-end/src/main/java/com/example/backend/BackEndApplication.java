package com.example.backend;

import com.example.backend.entities.Role;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@SpringBootApplication
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
//        System.out.println(Role.admin);
        System.out.println(Date.from(LocalDateTime.of(2022, 1,8,10,10).atZone(ZoneId.systemDefault()).toInstant()));
    }

}
