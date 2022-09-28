package com.example.backend.security;

import com.example.backend.repositories.UserRepository;
import com.example.backend.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserRepository userRepository;
    private Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
//    private BCryptPasswordEncoder argon2PasswordEncoder = new BCryptPasswordEncoder();
    private UserService userService;

    public SecurityConfig(UserRepository userRepository, UserService userService){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("Securityconfigure");
        auth.userDetailsService(userService).passwordEncoder(argon2PasswordEncoder);
//        auth.userDetailsService(username -> (UserDetails) userRepository.findByUserName(username));
//        auth.userDetailsService(userService).passwordEncoder(argon2PasswordEncoder);
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("CORS configure");
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("*"));
//        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
//        configuration.setAllowedHeaders(List.of("Authorization","content-type","IsRefreshToken"));
//        http.cors().configurationSource(request -> configuration).and();
        http.cors().and();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.addFilter(new JWTAuthenticationFilter(authenticationManagerBean()))
        http.authorizeRequests().anyRequest().permitAll();
//        http.authorizeRequests().antMatchers("/api/login").permitAll();
//        http.authorizeRequests().antMatchers("/api/refresh").permitAll();
        http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.authorizeRequests().antMatchers("/api/users/**").hasAnyAuthority("admin");

//        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/users/**").permitAll();
//        http.authorizeRequests().antMatchers("/api/users/**").authenticated();

//        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/**").authenticated();
//        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/users/**").authenticated();
//        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/users/**").authenticated();
//        http.authorizeRequests().anyRequest().authenticated();
//        http.authorizeRequests().antMatchers("/api/**").permitAll();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
