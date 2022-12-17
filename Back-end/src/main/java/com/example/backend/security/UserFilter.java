package com.example.backend.security;

import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@WebFilter(filterName = "UserFilter")
public class UserFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().startsWith("/api/users")) {
            String authorizationHeader = request.getHeader("Authorization");
            if(authorizationHeader!=null){
                String token = authorizationHeader.substring("Bearer ".length());
                Map claims = JWT.decode(token).getClaims();
                String role = claims.get("role").toString().replace("\"","");
                if(role.equals("admin")){ //UserFilter : Admin can do any request in users
                    response.setStatus(200);
                    filterChain.doFilter(request,response);
                } else{ //You aren't admin
                    response.setStatus(403);
                    response.getWriter().print("Unauthorized.");
                    return;
                }
            } else { //guest can do nothing
                response.setStatus(401);
                response.getWriter().print("Token is required.");
                return;
            }
        }
        else{
            filterChain.doFilter(request,response);
        }
    }
}
