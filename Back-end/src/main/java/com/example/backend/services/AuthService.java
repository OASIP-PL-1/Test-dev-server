package com.example.backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.backend.dtos.UserLoginDTO;
import com.example.backend.entities.User;
import com.example.backend.exception.ErrorDetails;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@EnableWebSecurity
@Service
public class AuthService {
    @Autowired
    UserRepository repository;

    @Value("${jwt.secret}")
    private String secret;

    private JWTVerifier refreshTokenVerifier;

    Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();

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
            message.put("userName", user.getUserName());
            message.put("userRole", user.getUserRole().toString());
            message.put("userId", user.getId().toString());
            message.put("userEmail", user.getUserEmail());
            message.put("accessToken", generateAccessToken(user.getUserEmail(), user.getUserRole().toString()));
            message.put("refreshToken", generateRefreshToken(user.getUserEmail(), user.getUserRole().toString()));
            statusCode = 200;
            httpStatus = HttpStatus.ACCEPTED;
        }
        return new ErrorDetails(new Date(),statusCode,httpStatus,message);
    }

    public ErrorDetails refresh(String refreshToken){
        System.out.println("Refresh token");
        HttpStatus httpStatus;
        Map message = new HashMap();
        Object token = null;
        try{
            token = JWT.decode(refreshToken);
            JWT.decode(refreshToken).getClaims();
            System.out.println(JWT.decode(refreshToken).getClaims().get("sub"));
        } catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid token");
        }
        if(JWT.decode(refreshToken).getExpiresAt().before(new Date())){
            message.put("message", "Token is expired.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is expired. Please login again");
        } else {
            Map claims = JWT.decode(refreshToken).getClaims();
            String userEmail = claims.get("sub").toString();
            String subUserEmail = userEmail.substring(1, userEmail.length() - 1);
            String role = claims.get("role").toString();
            String subRole = role.substring(1, role.length() - 1);
            message.put("message", "New token is available");
            message.put("accessToken", generateAccessToken(subUserEmail, subRole));
            message.put("refreshToken", generateRefreshToken(subUserEmail, subRole));
            httpStatus = HttpStatus.ACCEPTED;
        }
        return new ErrorDetails(new Date(), 200, httpStatus,message);
    }

    private String generateAccessToken(String userEmail, String userRole){
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String accessToken = JWT.create()
                .withSubject(userEmail)
                .withExpiresAt(new Date(System.currentTimeMillis() + 30*60*1000))
                .withClaim("role", userRole)
                .sign(algorithm);
        return accessToken;
    }

    private String generateRefreshToken(String userEmail, String userRole){
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String accessToken = JWT.create()
                .withSubject(userEmail)
                .withExpiresAt(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .withClaim("role", userRole)
                .sign(algorithm);
        return accessToken;
    }
}
