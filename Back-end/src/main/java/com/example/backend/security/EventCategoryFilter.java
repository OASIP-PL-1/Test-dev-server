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
public class EventCategoryFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().startsWith("/api/eventcategories")) {
//            if(request.getHeader("Authorization")!=null) {
//                String token = request.getHeader("Authorization").substring("Bearer ".length());
//                Map claims = JWT.decode(token).getClaims();
//                String role = claims.get("role").toString().replace("\"", "");
//                if(request.getMethod().equals(HttpMethod.POST)){
//                    if(role=="admin"){
//                        response.setStatus(200);
//                        System.out.println("Only admin can POST eventCategories");
//                        filterChain.doFilter(request, response);
//                    } else {
//                        response.setStatus(403);
//                        return;
//                    }
//                } else {
//                    response.setStatus(200);
//                    System.out.println("Any role can access eventCategories");
//                    filterChain.doFilter(request, response);
//                }
//            }
            filterChain.doFilter(request,response);
        } else {
            filterChain.doFilter(request,response);
        }
    }

}
