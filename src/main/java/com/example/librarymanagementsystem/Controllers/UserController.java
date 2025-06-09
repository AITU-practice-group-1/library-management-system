package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserServices userServices;
    public UserController(UserServices userServices)
    {
        this.userServices = userServices;
    }
    @GetMapping("/login")
    private String loginPage(Model model){
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    private String login(@ModelAttribute("user") User user){
        System.out.println(user.getEmail());
        try{
            userServices.login(user);
        }
        catch (Exception e){
            return "redirect:/users/login?error=true";
        }
        return "redirect:/books";
    }
}
