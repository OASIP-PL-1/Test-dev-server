package com.example.backend.dtos;

import com.example.backend.validator.EmailConstraint;
import com.example.backend.validator.RoleConstraint;
import com.example.backend.validator.UserEmailConstraint;
import com.example.backend.validator.UserNameConstraint;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddDTO {
    @NotBlank(message = "Username is required.")
    @Size(max = 100, message = "Username is too long, maximum 100 characters.")
    @UserNameConstraint
    private String userName;

    @NotBlank(message = "User's email is requied.")
    @Size(max = 50, message = "User's email is too long, maximum 50 characters")
    @EmailConstraint
    @UserEmailConstraint
    private String userEmail;

    @NotNull(message = "User role is required.")
    @NotBlank(message = "User role is required.")
    @RoleConstraint
    private String userRole;

    @NotBlank(message = "User password is required.")
    @Size(max = 14, message = "User password is out of length, expected lesser than 14 characters.")
    @Size(min = 8, message = "User password is too short, expected longer than 8 characters.")
    private String userPassword;


}
