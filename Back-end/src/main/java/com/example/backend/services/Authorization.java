package com.example.backend.services;

import com.auth0.jwt.JWT;

import javax.servlet.http.HttpServletRequest;

public class Authorization {

    public String getRoleFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        String role = JWT.decode(token).getClaims().get("role").toString();
        return role.replace("\"","");
    }

    public String getUserEmailFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        String userEmail = JWT.decode(token).getSubject();
        return userEmail;
    }

}
