package com.example.backend.controllers;

import com.example.backend.dtos.UserLoginDTO;
import com.example.backend.exception.ErrorDetails;
import com.example.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.http.HttpResponse;

@EnableWebSecurity
@RestController
@RequestMapping("")
// @CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public ErrorDetails logIn(@Valid @RequestBody UserLoginDTO login) {
        return service.logIn(login);
    }


}
