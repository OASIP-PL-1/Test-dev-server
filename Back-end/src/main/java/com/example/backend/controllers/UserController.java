package com.example.backend.controllers;

import com.example.backend.dtos.*;
import com.example.backend.exception.ErrorDetails;
import com.example.backend.services.UserService;
import com.example.backend.validator.EmailConstraint;
import com.example.backend.validator.RoleConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("")
    public List<UserAllDTO> getAllUserDTO() {
        return service.getUserAllDTO();
    }

    @GetMapping("/{userId}")
    public UserDTO getUserDTOById(@PathVariable @NotBlank(message = "User id is required for searching.") int userId) {
        return service.getUserDTOById(userId);
    }

    @PostMapping("")
    public int createUser(@Valid @RequestBody UserAddDTO newUser) {
        return service.createUser(newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) { service.deleteUser(userId); }

    @PutMapping("")
    public UserDTO editUser(@RequestBody UserUpdateDTO updateUser) {
        return service.editUser(updateUser);
    }

//    @PostMapping("/log-in")
//    public ErrorDetails logIn(@RequestBody UserLogin login) {
//        return service.logIn(login);
//    }
}
