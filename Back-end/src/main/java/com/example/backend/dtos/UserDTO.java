package com.example.backend.dtos;

import com.example.backend.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int id;
    private String userName;
    private String userEmail;
    private Role userRole;
    private Date createdOn;
    private Date updatedOn;
    public ZonedDateTime getCreatedOn(){
        return ZonedDateTime.ofInstant(createdOn.toInstant(), ZoneId.of("Asia/Bangkok"));
    }
    public ZonedDateTime getUpdatedOn(){
        return ZonedDateTime.ofInstant(createdOn.toInstant(), ZoneId.of("Asia/Bangkok"));
    }
}
