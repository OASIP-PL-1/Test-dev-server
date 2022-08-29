package com.example.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "userId", nullable = false)
    private Integer id;

    @Column(name = "userName", nullable = false, length = 100)
    private String userName;

    @Column(name = "userEmail", nullable = false, length = 50)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "userRole", nullable = false, length = 8)
    private Role userRole;

    @Column(name = "createdOn", nullable = false)
    private Date createdOn;

    @Column(name = "updatedOn", nullable = false)
    private Date updatedOn;

    @Column(name = "userPassword", nullable = false, length = 90)
    private String userPassword;

    public String toString(){
        return "User{" + userName + " " + userEmail + " " + userRole + " " + createdOn + " " + updatedOn + "}";
    }

}