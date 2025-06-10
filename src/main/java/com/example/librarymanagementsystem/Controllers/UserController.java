package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.UserDTO;
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
    @GetMapping("/home")
    private String homePage(){
        return "user-home";
    }
    @GetMapping("/register")
    private String loginPage(Model model){
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    private String login(@ModelAttribute("user") UserDTO dto, Model model){
        try{
            userServices.register(dto);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        return "redirect:/books";
    }

    @GetMapping("/login")
    private String login(){
        return "login";
    }
}
