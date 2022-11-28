package com.example.backend;

import com.auth0.jwt.JWT;
import com.example.backend.entities.EmailDetails;
import com.example.backend.entities.Role;
import com.example.backend.services.EmailServiceImpl;
import com.example.backend.services.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);


//        String name = "003-helenS.jpg";
//        System.out.println(name);
//
//        String[] s = name.split("[.]");
//        System.out.println(s.length);
//        System.out.println(s[0]);
//        System.out.println(s[1]);

//        EmailDetails e = new EmailDetails();
//        e.setRecipient("toon.pi45@gmail.com");
//        e.setMsgBody("hello this is body");
//        e.setSubject("hello this is subject");
//        EmailServiceImpl esi = new EmailServiceImpl();
//        System.out.println(esi.sendSimpleMail(e));

//        System.out.println(Role.admin);
//        System.out.println(Date.from(LocalDateTime.of(2022, 1,8,10,10).atZone(ZoneId.systemDefault()).toInstant()));
//
//        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
//        String result = encoder.encode("MyPassword");
//        System.out.println(result);
    }

}
