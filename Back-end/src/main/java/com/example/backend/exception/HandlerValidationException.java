package com.example.backend.exception;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class HandlerValidationException {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDetails handleInvalidArgument(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response){
        Map<String,String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(),error.getDefaultMessage());
        });
        ErrorDetails errorDetails= new ErrorDetails(new Date(), 400, HttpStatus.BAD_REQUEST, errorMap);
//        if(request.getServletPath().equals("/api/events")&&(request.getMethod().equals(HttpMethod.POST.toString()) || request.getMethod().equals(HttpMethod.PUT.toString()) )){
//            request.
//        }
        return errorDetails;
    }

}
