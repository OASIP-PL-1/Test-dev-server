package com.example.backend;

import com.auth0.jwt.JWT;
import com.example.backend.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@SpringBootApplication
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);

//        System.out.println(Role.admin);
//        System.out.println(Date.from(LocalDateTime.of(2022, 1,8,10,10).atZone(ZoneId.systemDefault()).toInstant()));
//
//        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
//        String result = encoder.encode("MyPassword");
//        System.out.println(result);
    }

}
