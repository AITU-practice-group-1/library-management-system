package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private  final UserServices userServices;
    public AdminController(UserServices userServices){
        this.userServices = userServices;
    }

    @GetMapping("/home")
    public String homePage(Model model){
        List<User> allUsers;
        try{
            allUsers = userServices.getAllUsers();
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        model.addAttribute("users", allUsers);
        return "user/admin-home";
    }

    @GetMapping("/home/default")
    public String showDefaultPage(Model model){
        List<User> allUsersDefault;
        try{
            allUsersDefault = userServices.getAllUsersByRole("DEFAULT");
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        model.addAttribute("users", allUsersDefault);
        return "user/admin-home";
    }
    @GetMapping("/home/librarian")
    public String showLibrarianPage(Model model){
        List<User> allUsersLibrarian;
        try{
            allUsersLibrarian = userServices.getAllUsersByRole("LIBRARIAN");
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        model.addAttribute("users", allUsersLibrarian);
        return "user/admin-home";
    }
}
