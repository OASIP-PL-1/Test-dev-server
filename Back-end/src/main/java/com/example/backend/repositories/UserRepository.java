package com.example.backend.repositories;

import com.example.backend.dtos.UserAddDTO;
import com.example.backend.entities.Role;
import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository <User, Integer> {
    public User findTopByOrderByIdDesc();
//    public User findTopByIdOrderByIdDesc();
    public User findByUserName(String userName);
    public User findByUserEmail(String userEmail);

    @Query(value = "INSERT INTO users(userName,userEmail,userRole) values (:userName, :userEmail, :userRole)",
            nativeQuery = true)
    @Modifying
    @Transactional
    public void createUser(@Param("userName")String userName,
                           @Param("userEmail")String userEmail,
                           @Param("userRole")String userRole);

}
