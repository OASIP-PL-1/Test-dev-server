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
public class EventFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().startsWith("/api/events")) {
            String token = request.getHeader("Authorization").substring("Bearer ".length());
            Map claims = JWT.decode(token).getClaims();
            String role = claims.get("role").toString().replace("\"","");
//            System.out.println(role);
            if(role.equals("admin")){
                response.setStatus(200);
                System.out.println("EventFilter : Admin can do ANY request in events.");
                filterChain.doFilter(request,response);
            }
            else if(role.equals("student")){
                response.setStatus(200);
                System.out.println("EventFilter : Student can GET his own assigned events only.");
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(403);
                response.getWriter().print("Unauthorized.");
                System.out.println("EventFilter : You aren't admin.");
                return;
            }
        }
        else{
            filterChain.doFilter(request,response);
        }
    }

}
