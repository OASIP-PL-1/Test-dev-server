package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.exception.ErrorDetails;
import org.hibernate.annotations.Filter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

@Component
@WebFilter()
public class AuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("dofilterInternal");
        System.out.println(request.getServletPath());
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/refresh")) {
            System.out.println("/api/login");
            filterChain.doFilter(request, response);
        }
        else {
            String authorizationHeader = request.getHeader("Authorization");
            System.out.println(authorizationHeader + "|");
//            if (authorizationHeader.startsWith("Bearer ") || authorizationHeader!="Bearer null" || authorizationHeader!=null) {
            if((authorizationHeader!=null) && (authorizationHeader.startsWith("Bearer"))){
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    System.out.println("try");
                    System.out.println("hnhnhnhnhnhnh");
                    Object JWTtoken = JWT.decode(token);
//                    System.out.println(JWTtoken);
//                    System.out.println(JWT.decode(token).getClaims().get("role"));
//                    JWT.decode(token).getClaims();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    response.setStatus(401);
                    response.getWriter().print("Invalid token.");
                    return;
                }
                String token = authorizationHeader.substring("Bearer ".length());
                System.out.println("try");
                if (JWT.decode(token).getExpiresAt().before(new Date())) {
                    response.setStatus(401);
                    response.getWriter().print("Token is expired.");
                    return;
                }
                response.setStatus(200);
                System.out.println("Pass all");
                filterChain.doFilter(request, response);
            } else {
                System.out.println("Token is null. Required.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Token is required.");
                return;
            }
        }
    }
}
