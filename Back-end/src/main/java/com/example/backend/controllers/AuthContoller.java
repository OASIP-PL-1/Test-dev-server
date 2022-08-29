package com.example.backend.controllers;

import com.example.backend.dtos.UserLoginDTO;
import com.example.backend.exception.ErrorDetails;
import com.example.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api")
// @CrossOrigin(origins = "*")
public class AuthContoller {
    @Autowired
    private AuthService service;

    @PostMapping("/match")
    public ErrorDetails logIn(@Valid @RequestBody UserLoginDTO login) {
        return service.logIn(login);
    }


}
