package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.reservations.ReservationsResponseDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.EmailTokenService;
import com.example.librarymanagementsystem.Services.ReservationsServices;
import com.example.librarymanagementsystem.Services.UserServices;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserServices userServices;
    private final EmailTokenService emailTokenService;
    private final ReservationsServices reservationsServices;
    public UserController(UserServices userServices, EmailTokenService emailTokenService, ReservationsServices reservationsServices)
    {
        this.userServices = userServices;
        this.emailTokenService = emailTokenService;
        this.reservationsServices = reservationsServices;
    }
    @GetMapping("/home")
    private String homePage(Model model){
        try{
            UserDTO responceDTO = userServices.getAuthenticatedUser();
            model.addAttribute("user", responceDTO);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/user-home";
        }
        return "user/user-home";
    }
    @GetMapping("/register")
    private String loginPage(Model model){
        model.addAttribute("user", new UserDTO());
        return "user/register";
    }

    @Validated
    @PostMapping("/register")
    private String login(@Valid @ModelAttribute("user") UserDTO dto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            model.addAttribute("errors", errors);
                return "user/register";
        }
        try{
            userServices.register(dto);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/register";
        }
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    private String login(){
        return "user/login";
    }

    @GetMapping("/edit")
    private String editPage(Model model){
        try{
            UserDTO dto = userServices.getAuthenticatedUser();
            model.addAttribute("user", dto);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/user-home";
        }
        return "user/user-edit";
    }

    @PostMapping("/edit")
    private String edit(@ModelAttribute("user") UserDTO dto, Model model){
        System.out.println(dto.getFirstName());
        try{
            userServices.updateUser(dto);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/user-edit";
        }
        return "redirect:/users/home";
    }

    @GetMapping("/profile/{id}")
    private String profile(@PathVariable UUID id, Model model)
    {
        try {
            model.addAttribute("user", userServices.getUserById(id));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "user/profile";
    }

    @GetMapping("/confirm")
    private String confirm(@RequestParam("token") String token, Model model)
    {
        try{
            emailTokenService.confirmToken(token);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/login";
    }

    @GetMapping("librarian")
    private String librarianPage(Model model) throws Exception {
        List<ReservationsResponseDTO> reservations = reservationsServices.listAllWthEmail();
        model.addAttribute("reservations", reservations);
        return "user/librarian-home";
    }
}
