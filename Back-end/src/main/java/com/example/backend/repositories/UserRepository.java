package com.example.backend.repositories;

import com.example.backend.dtos.UserAddDTO;
import com.example.backend.entities.Role;
import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository <User, Integer> {
    public User findTopByOrderByIdDesc();
    public User findByUserName(String userName);
    public User findByUserEmail(String userEmail);

    @Query(value = "INSERT INTO users(userName,userEmail,userRole,userPassword) values (:userName, :userEmail, :userRole, :userPassword)",
            nativeQuery = true)
    @Modifying
    @Transactional
    public void createUser(@Param("userName")String userName,
                           @Param("userEmail")String userEmail,
                           @Param("userRole")String userRole,
                           @Param("userPassword")String userPassword);

    @Query(value = "UPDATE users SET userName = :userName, userEmail = :userEmail, userRole = :userRole, updatedOn = now() WHERE userId = :userId",
            nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    public void editUser(@Param("userId") int userId,
                         @Param("userName") String userName,
                         @Param("userEmail") String userEmail,
                         @Param("userRole") String userRole);

    @Query(value="COMMIT", nativeQuery = true)
    @Transactional
    public void commit();

    @Query(value = "SELECT COUNT(*) FROM users WHERE userName = :userName AND userId != :userId",
           nativeQuery = true)
    public int checkUserNameExisted(@Param("userName") String userName,
                                    @Param("userId") int userId);

    @Query(value = "SELECT COUNT(*) FROM users WHERE userEmail = :userEmail AND userId != :userId",
            nativeQuery = true)
    public int checkUserEmailExisted(@Param("userEmail") String userEmail,
                                    @Param("userId") int userId);

    @Query(value = "select count(*)\n" +
            "from users u JOIN eventCategoryOwners e on u.userId = e.userId\n" +
            "where u.userId = :userId",
            nativeQuery = true)
    public int checkUserTeachEventCategoryOwner(@Param("userId") int userId);
}
