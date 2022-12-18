package com.example.backend.security;

import com.auth0.jwt.JWT;
import org.springframework.http.HttpMethod;
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
@WebFilter()
public class FileFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().startsWith("/api/files")) {
            String authorizationHeader = request.getHeader("Authorization");
            if(request.getMethod().equals(HttpMethod.DELETE)){
                if(authorizationHeader == null) { //guest can't delete
                    response.setStatus(401);
                    response.getWriter().print("Token is required for deleting file.");
                    return;
                } else {
                    if (!authorizationHeader.equals("Bearer null")) {
                        String token = authorizationHeader.substring("Bearer ".length());
                        Map claims = JWT.decode(token).getClaims();
                        String role = claims.get("role").toString().replace("\"", "");
                        if(role.equals("admin") || role.equals("student") || role.equals("lecturer")) filterChain.doFilter(request, response);
                    }
                }
            } else { //methods others than DELETE
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request,response);

        }
    }
}
