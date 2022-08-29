package com.example.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserLoginDTO {
    @NotBlank(message = "User's email is requied.")
    @Size(max = 50, message = "User's email is too long, maximum 50 characters")
    private String userEmail;
    
    @NotBlank(message = "User password is required.")
    @Size(max = 14, message = "User password is out of length, expected lesser than 14 characters.")
    @Size(min = 8, message = "User password is too short, expected longer than 8 characters.")
    private String userPassword;
}
