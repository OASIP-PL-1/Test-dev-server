package com.example.backend.services;

import com.example.backend.dtos.UserLoginDTO;
import com.example.backend.entities.User;
import com.example.backend.exception.ErrorDetails;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    UserRepository repository;

    Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();

    public ErrorDetails logIn(UserLoginDTO login){
        Map message = new HashMap<String,String>();
        int statusCode;
        HttpStatus httpStatus;
        User user = repository.findByUserEmail(login.getUserEmail().trim());
        boolean match = user != null && !encoder.matches(login.getUserPassword(), user.getUserPassword());
        if(user == null){
            message.put("message", "A user with specified email DOES NOT existed.");
            statusCode = 404;
            httpStatus = HttpStatus.NOT_FOUND;
        } else if(match){
            message.put("message", "Password NOT matched.");
            statusCode = 401;
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
            message.put("message", "Password matched.");
            statusCode = 200;
            httpStatus = HttpStatus.ACCEPTED;
        }
        return new ErrorDetails(new Date(),statusCode,httpStatus,message);
    }
}
