package com.example.librarymanagementsystem.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class Admin2FAInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("verified2FA") == null) {
            session = request.getSession(true);
            session.setAttribute("tempUser", request.getUserPrincipal().getName());
            response.sendRedirect("/auth");
            return false;
        }
        return true;
    }
}
