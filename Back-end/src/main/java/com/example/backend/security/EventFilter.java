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
            if (authorizationHeader == null) {
                System.out.println("Guest's token is null");
                if(request.getMethod().equals(HttpMethod.POST.toString())){
                    System.out.println("Guest can create event only");
                    filterChain.doFilter(request,response);
                } else {
                    response.setStatus(401);
                    response.getWriter().print("Token is required.");
                    return;
                }
            } else {
                if (!authorizationHeader.equals("Bearer null")) {
                    String token = authorizationHeader.substring("Bearer ".length());
                    Map claims = JWT.decode(token).getClaims();
                    String role = claims.get("role").toString().replace("\"", "");
//            System.out.println(role);
                    if (role.equals("admin")) {
                        response.setStatus(200);
                        System.out.println("EventFilter : Admin can do ANY request in events.");
                        filterChain.doFilter(request, response);
                    } else if (role.equals("student")) {
                        response.setStatus(200);
                        System.out.println("EventFilter : Student can GET his own assigned events only.");
                        filterChain.doFilter(request, response);
                    } else if (role.equals("lecturer")) {
                        if (request.getMethod().equals(HttpMethod.GET.toString())) {
                            response.setStatus(200);
                            System.out.println("EventFilter : lecturer can GET events that he taught only.");
                            filterChain.doFilter(request, response);
                        } else {
                            response.setStatus(403);
                            response.getWriter().print("Unauthorized.");
                            System.out.println("Lecturer isn't allow to used this method");
                            return;
                        }
                    }
                } else {
                    System.out.println("Guest is accessing /api/events");
                    if(request.getMethod().equals(HttpMethod.POST.toString())){
                        System.out.println("Guest can create event only");
                        filterChain.doFilter(request,response);
                    } else {
                        response.setStatus(401);
                        response.getWriter().print("Token is required.");
                        return;
                    }
                }
            }
        }
        else{
            filterChain.doFilter(request,response);
        }
    }
}


