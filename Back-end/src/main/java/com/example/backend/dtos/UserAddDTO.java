package com.example.backend.dtos;

import com.example.backend.entities.Role;
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
//    @Min(value = 1, message = "The least id number is 1.")
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private int id;

    @NotBlank(message = "Username is required.")
    @Size(max = 100, message = "Username is too long, maximum 100 characters.")
    @UserNameConstraint
    private String userName;

    @NotBlank(message = "User's email is requied.")
    @Size(max = 50, message = "User's email is too long, maximum 50 characters")
//    @Email(message = "Email is not valid.")
    @EmailConstraint
    @UserEmailConstraint
    private String userEmail;

    @NotNull(message = "User role is required.")
    @NotBlank(message = "User role is required.")
//    @Size(max = 8, min = 6, message = "Input role is out of length.")
//    @RoleSubset(anyOf = {Role.admin, Role.lecturer, Role.student})
//    @ValueOfEnum(enumClass = Role.class)
//    @RoleConstraintssss
    @RoleConstraint
    private String userRole;


}
