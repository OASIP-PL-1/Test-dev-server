package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
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
import java.util.ArrayList;
import java.util.Collection;

@Component
@WebFilter()
public class AuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("dofilterInternal");
//        filterChain.doFilter(request,response);
//        System.out.println(request.getHeader("Authorization"));
        System.out.println(request.getServletPath());
        if (request.getServletPath().equals("/api/login")) {
            System.out.println("filter login");
            filterChain.doFilter(request, response);
        }
        else {
            String authorizationHeader = request.getHeader("Authorization");
            String testGetHeader = request.getHeader("Referer");
            System.out.println("accesstoken :" + authorizationHeader);
            System.out.println("Referer :"+ testGetHeader);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
                try {
                    System.out.println("try");
                    String token = authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String role = decodedJWT.getClaim("role").asString();
                    System.out.println("Role: " + role);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(role));
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception ex) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    System.out.println("catch" + ex.getMessage());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                filterChain.doFilter(request, response);
            }
        }
//        response.setStatus(HttpServletResponse.SC_OK);
//        filterChain.doFilter(request,response);
    }
}
