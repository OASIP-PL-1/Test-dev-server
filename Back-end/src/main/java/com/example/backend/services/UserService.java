package com.example.backend.services;

import com.example.backend.dtos.UserAddDTO;
import com.example.backend.dtos.UserAllDTO;
import com.example.backend.dtos.UserDTO;
import com.example.backend.entities.Role;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    //GET method
    public List<UserAllDTO> getUserAllDTO() {
        List<User> users = repository.findAll(Sort.by("userName").ascending());
        return listMapper.mapList(users, UserAllDTO.class, modelMapper);
    }

    public UserDTO getUserDTOById(int userId) {
        User user = repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choosen user is not existed."));
        return modelMapper.map(user, UserDTO.class);
    }

    public int createUser(UserAddDTO newUser) {
        repository.createUser(newUser.getUserName().trim(), newUser.getUserEmail().trim(), newUser.getUserRole().toString().trim());
        return repository.findTopByOrderByIdDesc().getId();
    }

    public void deleteUser(int userId){
        repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Choosen user is not existed."));
        repository.deleteById(userId);
    }

}
