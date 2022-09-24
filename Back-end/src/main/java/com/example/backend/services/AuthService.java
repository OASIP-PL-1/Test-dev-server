package com.example.backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.backend.dtos.UserLoginDTO;
import com.example.backend.entities.User;
import com.example.backend.exception.ErrorDetails;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@EnableWebSecurity
@Service
public class AuthService {
    @Autowired
    UserRepository repository;

    Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ErrorDetails logIn(UserLoginDTO login){
        System.out.println("ErrorDetails logIn");
        Map message = new HashMap<String,String>();
        int statusCode;
        HttpStatus httpStatus;
        User user = repository.findByUserEmail(login.getUserEmail().trim());
        boolean match = user != null && !encoder.matches(login.getUserPassword(), user.getUserPassword());
        if(user == null){
            message.put("message", "A user with specified email DOES NOT existed.");
            statusCode = 404;
            httpStatus = HttpStatus.NOT_FOUND;
        } else if(match){
            message.put("message", "Password NOT matched.");
            statusCode = 401;
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
            message.put("message", "Password matched.");
            message.put("accessToken", generateJWT(user));
            statusCode = 200;
            httpStatus = HttpStatus.ACCEPTED;
        }
        return new ErrorDetails(new Date(),statusCode,httpStatus,message);
    }

    private String generateJWT(User user){
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        System.out.println("GenerateToken");
        String accessToken = JWT.create()
                .withSubject(user.getUserEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30*60*1000))
                .withClaim("role", user.getUserRole().toString())
                .sign(algorithm);
        System.out.println(accessToken);
        return accessToken;
    }
}
