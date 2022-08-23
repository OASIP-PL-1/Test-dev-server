package com.example.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private HttpStatus status;
    private Map<String,String> message;

}
