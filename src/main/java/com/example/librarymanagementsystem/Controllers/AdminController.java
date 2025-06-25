package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.Users.UserRegisterDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.UserServices;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private  final UserServices userServices;
    public AdminController(UserServices userServices){
        this.userServices = userServices;
    }

    @GetMapping("/home")
    public String homePage(Model model, @PageableDefault(size = 10) Pageable pageable){
        Page<User> allUsers;
        try{
            allUsers = userServices.getAllUsersPaginated(pageable);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        model.addAttribute("users", allUsers);
        return "user/admin-home";
    }

    @GetMapping("/home/default")
    public String showDefaultPage(Model model, @PageableDefault(size = 10) Pageable pageable){
        Page<User> allUsersDefault;
        try{
            allUsersDefault = userServices.getAllUsersByRole("DEFAULT", pageable);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        model.addAttribute("users", allUsersDefault);
        return "user/admin-home";
    }

    @GetMapping("/home/librarian")
    public String showLibrarianPage(Model model, @PageableDefault(size = 10) Pageable pageable){
        Page<User> allUsersLibrarian;
        try{
            allUsersLibrarian = userServices.getAllUsersByRole("LIBRARIAN", pageable);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        model.addAttribute("users", allUsersLibrarian);
        return "user/admin-home";
    }

    @GetMapping("/add/user")
    public String addUserPage(Model model){
        model.addAttribute("user", new User());
        return "user/add-user";
    }

    @PostMapping("/add/user")
    private String add(@Valid @ModelAttribute("user") UserDTO dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            model.addAttribute("errors", errors);
            return "user/register";
        }
        try{

            userServices.addUser(dto);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/register";
        }
        redirectAttributes.addFlashAttribute("successRegistration", "You registered successfully, please check your email to activate your account!");
        return "redirect:/admin/home";
    }

    @GetMapping("/edit/user")
    public String editUserPage(Model model, @RequestParam("id") UUID id){
        try{
            UserDTO dto = userServices.getUserDTOById(id);
            model.addAttribute("user", dto);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        return "user/admin-user-edit";
    }

    @PostMapping("/edit/user")
    public String editUser(@RequestParam("id") UUID id, @ModelAttribute("user") UserDTO dto, Model model){
        try{
            userServices.updateUser(dto, true);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/home";
    }


}
