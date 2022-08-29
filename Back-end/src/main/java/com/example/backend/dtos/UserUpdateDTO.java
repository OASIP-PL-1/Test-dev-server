package com.example.backend.dtos;

import com.example.backend.entities.Role;
import com.example.backend.validator.EmailConstraint;
import com.example.backend.validator.RoleConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    @Min(value = 1, message = "The least id number possible is 1.")
    private int id;

    @NotBlank(message = "Username is required.")
    @Size(max = 100, message = "Username is too long, maximum 100 characters.")
    private String userName;

    @NotBlank(message = "User's email is requied.")
    @Size(max = 50, message = "User's email is too long, maximum 50 characters")
    @EmailConstraint
    private String userEmail;

    @NotNull(message = "User role is required.")
    @NotBlank(message = "User role is required.")
    @RoleConstraint
    private Role userRole;

}
