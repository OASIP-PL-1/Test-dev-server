package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.backend.entities.Role;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.UserDetailImp;
import com.example.backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        String token = request.getParameter("accessToken");
//        System.out.println("doFilter JWTFilter");
//        chain.doFilter(request,response);
//    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
//        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("AttemptAuthentication");
//        try{
//            User creds = new ObjectMapper().readValue(request.getInputStream(), User.class);
//            System.out.println(creds);
//            UsernamePasswordAuthenticationToken a = new UsernamePasswordAuthenticationToken(
//                    creds.getUserEmail(),
//                    creds.getUserPassword(),
//                    new ArrayList<>());
//            System.out.println(a);
//            return authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            creds.getUserEmail(),
//                            creds.getUserPassword(),
//                            new ArrayList<>())
//            );
////                    new UsernamePasswordAuthenticationToken("a","1234", new ArrayList<>())
////            );
//        } catch (IOException ex){
//            throw new RuntimeException(ex);
//        }
        String userEmail = request.getParameter("userEmail");
        String password = request.getParameter("password");
        System.out.println(userEmail + password);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userEmail, password);
        System.out.println(token);
        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        String token = JWT.create()
//                .withSubject(authResult.get)
//        System.out.println("SuccesfulAuthen");
//        String token = JWT.create()
//                .withSubject(((User) authResult.getPrincipal()).getUserName())
//                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
//                .sign(Algorithm.HMAC256("secret"));
//
//        String body = ((User) authResult.getPrincipal()).getUserName() + "" + token;
//
//        response.getWriter().write(body);
//        response.getWriter().flush();

        System.out.println("SuccesfulAuthen");
        System.out.println(authResult.getPrincipal());
//        User user = new User();
//        user.setUserName("b");
//        user.setUserEmail("b@gmail.com");
//        user.setUserPassword("$argon2id$v=19$m=4096,t=3,p=1$+FzkPJ9qBcASVjSVAUnFYA$wDV0eaWzpgZfwyfTe/y4gkoCzF6Ju24RVTk9jcILerI");
        User user = (User) authResult.getPrincipal();
//        User user = service.getUserByUserEmail(request.getParameter("userEmail"));
//        User user = repository.findByUserEmail(request.getParameter("userEmail"));
        System.out.println(user.getUserName());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//        String[] role =
        ArrayList<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getUserRole().toString()));
        String accessToken = JWT.create()
                .withSubject(user.getUserEmail())
                .withExpiresAt(new Date(System.currentTimeMillis()+10*50*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
//                .withClaim("roles", user.getUserRole().toString().)

        response.setHeader("accessToken", accessToken);

    }
}
