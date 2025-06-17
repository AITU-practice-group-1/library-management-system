package com.example.librarymanagementsystem.security;

import com.example.librarymanagementsystem.Services.SessionStore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SingleDeviceAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SingleDeviceAuthenticationFilter.class);

    @Autowired
    private SessionStore sessionStore;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/users/login") || path.equals("/login") || path.startsWith("/css/") ||
                path.startsWith("/js/") || path.equals("/users/register") || path.equals("/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.debug("Processing request: {}", request.getRequestURI());
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            logger.debug("Session ID: {}", sessionId);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                logger.debug("Authenticated user: {}", userDetails.getUsername());
                System.out.print(sessionId + " " + userDetails.getUsername() + "\n");
                if (!sessionStore.isValidSession(sessionId)) {
                    logger.warn("Invalid session for user: {}. Logging out.", userDetails.getUsername());
                    SecurityContextHolder.clearContext();
                    session.invalidate();
                    response.sendRedirect("/users/login?sessionExpired=true");
                    return;
                } else {
                    logger.debug("Updating last active time for session: {}", sessionId);
                    sessionStore.updateLastActive(sessionId);
                }
            } else {
                logger.debug("Unauthenticated request for session: {}", sessionId);
            }
        } else {
            logger.debug("No session found for request: {}", request.getRequestURI());
        }
        chain.doFilter(request, response);
    }
}