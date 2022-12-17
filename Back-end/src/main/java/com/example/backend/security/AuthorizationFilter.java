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
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/refresh")) {;
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null) {
                filterChain.doFilter(request, response);
            }
            else { //check if token is valid
                if ((!authorizationHeader.equals("Bearer null")) && (authorizationHeader.startsWith("Bearer"))) {
                    try {
                        String token = authorizationHeader.substring("Bearer ".length());
                        Object JWTtoken = JWT.decode(token);
                    } catch (Exception ex) {
                        response.setStatus(401);
                        response.getWriter().print("Invalid token.");
                        return;
                    }
                    String token = authorizationHeader.substring("Bearer ".length());
                    if (JWT.decode(token).getExpiresAt().before(new Date())) {
                        response.setStatus(401);
                        response.getWriter().print("Token is expired.");
                        return;
                    }
                    response.setStatus(200);
                    filterChain.doFilter(request, response);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        }
    }
}
