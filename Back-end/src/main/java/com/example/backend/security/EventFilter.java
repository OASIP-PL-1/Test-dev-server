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
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null) { //guest can do nothing
                    response.setStatus(401);
                    response.getWriter().print("Token is required.");
                    return;
            } else {
                if (!authorizationHeader.equals("Bearer null")) {
                    String token = authorizationHeader.substring("Bearer ".length());
                    Map claims = JWT.decode(token).getClaims();
                    String role = claims.get("role").toString().replace("\"", "");
                    if (role.equals("admin")) { //EventFilter : Admin can do ANY request in events.
                        response.setStatus(200);
                        filterChain.doFilter(request, response);
                    } else if (role.equals("student")) { //EventFilter : Student can GET his own assigned events only
                        response.setStatus(200);
                        filterChain.doFilter(request, response);
                    } else if (role.equals("lecturer")) {
                        if (request.getMethod().equals(HttpMethod.GET.toString())) { //EventFilter : lecturer can GET events that he taught only
                            response.setStatus(200);
                            filterChain.doFilter(request, response);
                        } else { //Lecturer isn't allow to used this method
                            response.setStatus(403);
                            response.getWriter().print("Unauthorized.");
                            return;
                        }
                    }
                }
            }
        }
        else{
            filterChain.doFilter(request,response);
        }
    }
}


