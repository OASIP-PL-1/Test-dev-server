package com.example.backend.controllers;

import com.example.backend.dtos.UserAddDTO;
import com.example.backend.dtos.UserAllDTO;
import com.example.backend.dtos.UserDTO;
import com.example.backend.services.UserService;
import com.example.backend.validator.RoleConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
// @CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("")
    public List<UserAllDTO> getAllUserDTO() {
        return service.getUserAllDTO();
    }

    @GetMapping("/{userId}")
    public UserDTO getUserDTOById(@PathVariable int userId) {
        return service.getUserDTOById(userId);
    }

    @PostMapping("")
    public int createUser(@Valid @RequestBody UserAddDTO newUser) {
        return service.createUser(newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) { service.deleteUser(userId); }

}
