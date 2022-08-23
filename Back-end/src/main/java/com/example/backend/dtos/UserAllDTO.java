package com.example.backend.dtos;

import com.example.backend.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAllDTO {
    private int id;
    private String userName;
    private String userEmail;
    private Role userRole;
}
