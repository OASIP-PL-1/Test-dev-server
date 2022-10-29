package com.example.backend.services;

import com.example.backend.dtos.*;
import com.example.backend.entities.Role;
import com.example.backend.entities.User;
import com.example.backend.repositories.EventCategoryOwnerRepository;
import com.example.backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;

    Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    //GET method
    public List<UserAllDTO> getUserAllDTO() {
        List<User> users = repository.findAll(Sort.by("userName").ascending());
        return listMapper.mapList(users, UserAllDTO.class, modelMapper);
    }

    public UserDTO getUserDTOById(int userId) {
        User user = repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choosen user is not existed."));
//        User user = repository.findById(userId).orElseThrow(new ErrorDetails());
//        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException(404,HttpStatus.NOT_FOUND,"This user id is not existed."));
        return modelMapper.map(user, UserDTO.class);
    }


    //POST method
    public int createUser(UserAddDTO newUser) {
        repository.createUser(newUser.getUserName().trim(), newUser.getUserEmail().trim(), newUser.getUserRole().trim(), encoder.encode(newUser.getUserPassword().trim()));
        repository.findTopByOrderByIdDesc().getId();
        return repository.findTopByOrderByIdDesc().getId();
    }

//    public ErrorDetails logIn(UserLogin login){
//        Map message = new HashMap<String,String>();
//        int statusCode;
//        HttpStatus httpStatus;
//        User user = repository.findByUserEmail(login.getUserEmail().trim());
//        if(user == null){

//            message.put("message", "A user with specified email DOES NOT existed.");
//            statusCode = 404;
//            httpStatus = HttpStatus.NOT_FOUND;
//        } else if(!user.getUserPassword().equals(login.getUserPassword().trim())){
//            message.put("message", "Password NOT matched.");
//            statusCode = 401;
//            httpStatus = HttpStatus.UNAUTHORIZED;
//        } else {
//            message.put("message", "Password matched.");
//            statusCode = 200;
//            httpStatus = HttpStatus.ACCEPTED;
//        }
//        return new ErrorDetails(new Date(),statusCode,httpStatus,message);
//    }

    //DELETE method
    public void deleteUser(int userId){
        User user = repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choosen user is not existed."));
        if(repository.checkUserTeachEventCategoryOwner(userId)>0) {
            eventCategoryOwnerRepository.deleteByUser(user);
            repository.deleteById(userId);
        }
    }

    //PUT method
    public UserDTO editUser(UserUpdateDTO updateUser) {
        User oldUser = repository.findById(updateUser.getId()).orElseThrow();
        if(repository.checkUserNameExisted(updateUser.getUserName(), updateUser.getId()) > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This user name is already existed");
        if(repository.checkUserEmailExisted(updateUser.getUserEmail(), updateUser.getId()) > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This user email is already existed");
        if(updateUser.getUserName().equals(oldUser.getUserName()) &&
                updateUser.getUserEmail().equals(oldUser.getUserEmail()) &&
                updateUser.getUserRole().toString().equals(oldUser.getUserRole().toString())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There aren't any changes.");
        repository.editUser(updateUser.getId(), updateUser.getUserName(), updateUser.getUserEmail(),updateUser.getUserRole().toString());
//        repository.flush();
//        repository.saveAndFlush(new User(updateUser.getId(),updateUser.getUserName(),updateUser.getUserEmail(),updateUser.getUserRole(), new Date(), new Date(), "1234"));
//        repository.commit();

//        User user = repository.findById(updateUser.getId()).orElseThrow();
//        System.out.println(user);
//        System.out.println(modelMapper.map(user, UserDTO.class));
//        return modelMapper.map(user, UserDTO.class);
        return getUserDTOById(updateUser.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
//        System.out.println("UserService : LoadUserByUsername");
        User user = repository.findByUserEmail(userEmail);
        System.out.println("UserServiceLoadByUserName: " + userEmail);
//        if(user==null) user = repository.findById(8).orElseThrow();
        if(user==null) throw new ResourceNotFoundException();
//        System.out.println(user.getUserName());
//        Role[] roles = Role.values();
        ArrayList<SimpleGrantedAuthority> role = new ArrayList<>();
//        for (Role r : roles) {
//            role.add(new SimpleGrantedAuthority(r.toString()));
//        }
        role.add(new SimpleGrantedAuthority(user.getUserRole().toString()));
        System.out.println(role);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUserEmail(), user.getUserPassword(), role);
        System.out.println(userDetails.getPassword());
        return userDetails;
    }

}
