package com.example.backend.exception;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handle(int status, HttpStatus error, String message){
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        ErrorDetails errorDetails = new ErrorDetails(new Date(), status, error, map);
        return new ResponseEntity(errorDetails, HttpStatus.NOT_FOUND);
    }

}
