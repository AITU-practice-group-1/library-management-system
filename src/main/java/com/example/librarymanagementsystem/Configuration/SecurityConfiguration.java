package com.example.librarymanagementsystem.Configuration;

import com.example.librarymanagementsystem.Entities.TwoFactorAuthData;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.TwoFactorAuthRepository;
import com.example.librarymanagementsystem.Services.CustomUserDetailsService;
import com.example.librarymanagementsystem.Services.SessionStore;
import com.example.librarymanagementsystem.security.SingleDeviceAuthenticationFilter;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.librarymanagementsystem.security.Admin2FAFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
//import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.time.LocalDateTime;
import java.util.Objects;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private SingleDeviceAuthenticationFilter singleDeviceAuthenticationFilter;

    @Autowired

    private SessionStore sessionStore;

    @Autowired
    private TwoFactorAuthRepository twoFactorAuthRepository;

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
                .authorizeHttpRequests(auth -> auth
                        // CORRECTED: Added /** to permit all resources in these directories
                        .requestMatchers(
                                "/",
                                "/users/login",
                                "/users/register",
                                "users/confirm",
                                "/books",
                                "/books/{id}",
                                "/css/**",  // <-- FIX
                                "/js/**",   // <-- FIX
                                "/forgot-password",
                                "reset-password",
                                "/auth",
                                "/2fa/setup"
                        ).permitAll()
                        .requestMatchers("/ban/**", "/unban/**").hasAnyRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Good practice to use /admin/**
                        .anyRequest().authenticated()
                )
//                      .anyRequest().permitAll())
                .formLogin(form -> form
                                .loginPage("/users/login")
                                .permitAll()
                                .successHandler((request, response, authentication) -> {
                                    HttpSession session = request.getSession();
                                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                                    com.example.librarymanagementsystem.Entities.User user =
                                            customUserDetailsService.loadDomainUserByUsername(userDetails.getUsername());

                                    if (user.getRole().equalsIgnoreCase("ADMIN")) {
                                        TwoFactorAuthData data = twoFactorAuthRepository.findByUser(user).orElse(null);

                                        // --- START OF MODIFIED LOGIC ---
                                        // Only redirect to 2FA verification if it has been set up AND is enabled.
                                        if (data != null && data.isEnabled()) {
                                            logger.info("Admin {} has active 2FA. Redirecting to code entry.", user.getEmail());

                                            // Clear preliminary security context and store user temporarily for 2FA validation
                                            SecurityContextHolder.clearContext();
                                            session.removeAttribute("SPRING_SECURITY_CONTEXT");
                                            session.setAttribute("tempUser", user.getEmail());

                                            response.sendRedirect("/auth");
                                            return; // IMPORTANT: Stop further execution
                                        }
                                        // If admin has no 2FA or it's disabled, proceed to normal login below.
                                        // --- END OF MODIFIED LOGIC ---
                                    }
                                    String deviceInfo = request.getRemoteAddr() + " | " + request.getHeader("User-Agent");
                                    logger.info("User {} logged in successfully from {}", userDetails.getUsername(), deviceInfo);
                                    sessionStore.registerNewSession(userDetails.getUsername(), session.getId(), deviceInfo);

                                    RequestCache requestCache = new HttpSessionRequestCache();
                                    SavedRequest savedRequest = requestCache.getRequest(request, response);
                                    if(savedRequest != null)
                                    {
                                        String targetUrl = savedRequest.getRedirectUrl();
                                        // Clean up error flags from the URL if any
                                        if (targetUrl.contains("?error")) {
                                            targetUrl = "/";
                                        }
                                        response.sendRedirect(targetUrl);
                                    }
                                    else{
                                        if(Objects.equals(user.getRole(), "ADMIN")) {
                                            response.sendRedirect("/setup");
                                        }else {
                                            response.sendRedirect("/");
                                        }
                                    }
//                          response.sendRedirect("/books");x
                                })
                                .failureHandler((request, response, exception) -> {
                                    String username = request.getParameter("username");
                                    String ipAddress = request.getRemoteAddr();
                                    logger.warn("Login failed for user {} from IP {}: {}", username, ipAddress, exception.getMessage());
                                    request.getSession().setAttribute("error", "Invalid username or password");
                                    response.sendRedirect("/users/login?error=true");
                                })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Change to a unique URL to avoid static resource conflict
                        .logoutSuccessUrl("/")
                        .permitAll()
                        .addLogoutHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                                logger.info("User {} logged out from session {}", userDetails.getUsername(), session.getId());
                                sessionStore.invalidateSession(session.getId());
                            }
                        }))
                .addFilterBefore(singleDeviceAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex ->ex.accessDeniedPage("/error/403")).build();
    }
}