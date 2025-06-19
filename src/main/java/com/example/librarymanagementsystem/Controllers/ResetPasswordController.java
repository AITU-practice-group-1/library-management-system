package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ResetPasswordController {
    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String forgotPassword(){
        return "user/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model){
        try{
            passwordResetService.createPasswordResetToken(email);
            model.addAttribute("message", "Link for reset sent to email");
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
        }
        return "user/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "user/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            model.addAttribute("token", token);
            return "user/reset-password";
        }

        try {
            passwordResetService.passwordReset(token, password);
            model.addAttribute("message", "Пароль успешно сброшен");
            return "redirect:/users/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "user/reset-password";
        }
    }

}
