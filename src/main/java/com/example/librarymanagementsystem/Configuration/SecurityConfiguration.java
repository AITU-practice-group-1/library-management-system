package com.example.librarymanagementsystem.Configuration;

import com.example.librarymanagementsystem.Services.CustomUserDetailsService;
import com.example.librarymanagementsystem.Services.InMemorySessionStore;
import com.example.librarymanagementsystem.security.SingleDeviceAuthenticationFilter;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private SingleDeviceAuthenticationFilter singleDeviceAuthenticationFilter;

    @Autowired
    private InMemorySessionStore sessionStore;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
              //.csrf(AbstractHttpConfigurer::disable)
              //.cors(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth ->auth
                      .requestMatchers("/","/users/login", "/users/register","users/confirm", "/books","/books/{id}", "/css/**", "/js/**", "/forgot-password", "reset-password").permitAll()
                      .requestMatchers("/admin/*").hasRole("ADMIN")
                      .anyRequest().authenticated())
//                      .anyRequest().permitAll())
              .formLogin(form -> form
                      .loginPage("/users/login")
                      .permitAll()
                      .successHandler((request, response, authentication) -> {
                          HttpSession session = request.getSession();
                          UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                          String deviceInfo = request.getRemoteAddr() + " | " + request.getHeader("User-Agent");
                          //System.out.println(deviceInfo);
                          sessionStore.registerNewSession(userDetails.getUsername(), session.getId(), deviceInfo);
                          response.sendRedirect("/books");
                      })
              )
              .logout(logout -> logout
                      .logoutUrl("/logout") // Change to a unique URL to avoid static resource conflict
                      .logoutSuccessUrl("/")
                      .permitAll()
                      .addLogoutHandler((request, response, authentication) -> {
                          HttpSession session = request.getSession(false);
                          if (session != null) {
                              System.out.println(session.getId() + " is logged out");
                              sessionStore.invalidateSession(session.getId());
                          }
                      }))
              .addFilterBefore(singleDeviceAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
              .exceptionHandling(ex ->ex.accessDeniedPage("/error/403"))
              .build();
    }
}
