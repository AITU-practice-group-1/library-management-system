package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.TwoFactorAuthData;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Repositories.TwoFactorAuthRepository;
import com.example.librarymanagementsystem.Repositories.UserRepository;
import com.example.librarymanagementsystem.Services.GAService;
import com.example.librarymanagementsystem.Services.SessionStore;
import com.example.librarymanagementsystem.Services.impl.InMemorySessionStore;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class TwoFactorAuthController {
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthController.class);

    private final TwoFactorAuthRepository twoFactorAuthRepository;
    private final UserRepository userRepository;
    private final GAService gaService;
    private final SessionStore sessionStore; // ✅ добавь сюда

    // Показывает QR код для настройки 2FA
    @GetMapping("/setup")
    public String showSetup(Model model, Principal principal) {
        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Find the existing 2FA data.
        TwoFactorAuthData twoFA = twoFactorAuthRepository.findByUser(user)
                .orElse(null);

        GoogleAuthenticatorKey key;

        // --- START OF FIXED LOGIC ---
        if (twoFA == null) {
            // Case 1: No 2FA record exists for this user. Create a new one.
            logger.info("No 2FA record found for user {}. Creating a new one.", email);
            key = gaService.generateCredentials();
            String secret = key.getKey();

            twoFA = TwoFactorAuthData.builder()
                    .user(user)
                    .secret(secret)
                    .enabled(false) // It's not enabled until verified by the user
                    .build();

            twoFactorAuthRepository.save(twoFA); // This is safe now

        } else {
            // Case 2: A 2FA record already exists (whether it's enabled or not).
            // We simply use its existing secret to re-generate the QR code.
            // We do NOT create or save anything here.
            logger.info("Existing 2FA record found for user {}. Re-generating QR code.", email);
            key = new GoogleAuthenticatorKey.Builder(twoFA.getSecret()).build();
        }
        // --- END OF FIXED LOGIC ---


        String qrUrl = gaService.getQRUrl(user.getEmail(), key);
        model.addAttribute("qrUrl", qrUrl);
        // You can optionally add the status to the model to display a message in the view
        model.addAttribute("is2faEnabled", twoFA != null && twoFA.isEnabled());

        return "user/setup";
    }

    @GetMapping("/home")
    public String showAdminHome() {
        return "index"; // из src/main/resources/templates/admin-home.html
    }


    // Проверка кода после сканирования QR
    @PostMapping("/verify")
    public String verifyCode(@RequestParam("code") int code, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        TwoFactorAuthData data = twoFactorAuthRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (gaService.verify(data.getSecret(), code)) {
            data.setEnabled(true);
            twoFactorAuthRepository.save(data);
            return "user/activated"; // пользователь видит страницу об успешной активации
        }

        return "redirect:/setup?error"; // раньше было /user/2fa/setup
    }

    @GetMapping("/auth")
    public String show2FAForm() {
        return "user/2fa-auth";
    }

    @PostMapping("/auth")
    public void process2FA(
            @RequestParam("code") int code,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String username = (String) session.getAttribute("tempUser");

        if (username == null) {
            response.sendRedirect("/users/login?expired=true");
            return;
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        TwoFactorAuthData data = twoFactorAuthRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("2FA data not found"));

        if (data.isEnabled() && gaService.verify(data.getSecret(), code)) {
            // 🔑 Авторизация
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user, user.getPassword(), user.getAuthorities()
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            // ✅ Регистрация в SessionStore
            String deviceInfo = request.getRemoteAddr() + " | " + request.getHeader("User-Agent");
            sessionStore.registerNewSession(user.getEmail(), session.getId(), deviceInfo);

            // Метки
            session.setAttribute("verified2FA", true);
            session.removeAttribute("tempUser");

            System.out.println("✅ 2FA успешно пройден для: " + username);
            System.out.println("📦 SESSION ID: " + session.getId());

            response.sendRedirect("/home");
        } else {
            response.sendRedirect("/auth?error");
        }
    }


}


