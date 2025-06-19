package com.example.librarymanagementsystem.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

import com.example.librarymanagementsystem.Entities.User;
@Component
public class Admin2FAFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String path = req.getRequestURI();

        if (path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images")) {
            chain.doFilter(request, response);
            return;
        }

        // Только если идёт попытка получить доступ к админским страницам
        boolean isAuthPath = path.startsWith("/auth") || path.startsWith("/verify") || path.equals("/auth.html");

        if (session != null) {
            Boolean verified = (Boolean) session.getAttribute("verified2FA");
            String tempUser = (String) session.getAttribute("tempUser");

            if (tempUser != null && (verified == null || !verified)) {
                if (!isAuthPath) {
                    res.sendRedirect("/auth");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}

