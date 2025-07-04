package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.Users.UserRegisterDTO;
import com.example.librarymanagementsystem.DTOs.Users.UserStatisticsBookDTO;
import com.example.librarymanagementsystem.DTOs.reservations.ReservationsResponseDTO;
import com.example.librarymanagementsystem.Entities.User;
import com.example.librarymanagementsystem.Services.EmailTokenService;
import com.example.librarymanagementsystem.Services.ImageUploadService;
import com.example.librarymanagementsystem.Services.ReservationsServices;
import com.example.librarymanagementsystem.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserServices userServices;
    private final EmailTokenService emailTokenService;
    private final ReservationsServices reservationsServices;
    private final ImageUploadService imageUploadService;
//    public UserController(UserServices userServices, EmailTokenService emailTokenService, ReservationsServices reservationsServices)
//    {
//        this.userServices = userServices;
//        this.emailTokenService = emailTokenService;
//        this.reservationsServices = reservationsServices;
//    }
    @GetMapping("/home")
    private String homePage(Model model){
        logger.info("User with email{} entered the home page. at endpoint: /users/home", SecurityContextHolder.getContext().getAuthentication().getName());
        try{
            UserDTO responceDTO = userServices.getAuthenticatedUser();
            model.addAttribute("user", responceDTO);
            model.addAttribute("showStatistics", false);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/user-home";
        }
        return "user/user-home";
    }
    @GetMapping("/register")
    private String loginPage(Model model){
        logger.info("User entered the register page. at endpoint: /users/register");
        model.addAttribute("user", new UserDTO());
        return "user/register";
    }

    @Validated
    @PostMapping("/register")
    private String register(@Valid @ModelAttribute("user") UserRegisterDTO dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        logger.info("User posted information to  the register page. at endpoint: /users/register");
        if(bindingResult.hasErrors()){
            logger.info("User entered invalid information to  the register page. at endpoint: /users/register");
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
        redirectAttributes.addFlashAttribute("successRegistration", "You registered successfully, please check your email to activate your account!");
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    private String login(){
        logger.info("User entered the login page. at endpoint: /users/login");
        return "user/login";
    }

    @GetMapping("/edit")
    private String editPage(Model model){
        logger.info("User with email {} entered the edit page. at endpoint: /users/edit", SecurityContextHolder.getContext().getAuthentication().getName());
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
    private String edit(@RequestParam(value = "image", required = false) MultipartFile file ,@ModelAttribute("user") UserDTO dto, Model model){
        logger.info("User with email {} posted information to  the edit page. at endpoint: /users/edit", SecurityContextHolder.getContext().getAuthentication().getName());
        try{
            if(!file.isEmpty()){
                String imageUrl = imageUploadService.uploadUserFile(file);
                model.addAttribute("imageUrl", imageUrl);
            }

            userServices.updateUser(dto, false);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/user-edit";
        }
        return "redirect:/users/home";
    }

    @GetMapping("/profile/{id}")
    private String profile(@PathVariable UUID id, Model model, @PageableDefault(size = 10) Pageable pageable)
    {
        logger.info("User with email {} entered the profile page. at endpoint: /users/profile/{id}",
                SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            UserDTO userDTO = userServices.getUserDTOById(id);
            boolean isBanned = userServices.isUserBanned(id);

            model.addAttribute("user", userDTO);
            model.addAttribute("isBanned", isBanned);
            model.addAttribute("showStatistics", true);

            model.addAttribute("reservedBooks", userServices.getAllReservedBooksOfTheUser(id, pageable));
            model.addAttribute("loanedBooks", userServices.getAllLoanedBooksOfTheUser(id, pageable));
            model.addAttribute("readBooks", userServices.getAllReadBooksOfTheUser(id, pageable));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "user/profile"; // или "user/user-profile", если так называется шаблон
    }


    @GetMapping("/confirm")
    private String confirm(@RequestParam("token") String token, Model model)
    {
        logger.info("User entered the confirm page. at endpoint: /users/confirm");
        try{
            emailTokenService.confirmToken(token);
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/login";
    }

    @GetMapping("librarian")
    private String librarianPage(Model model) throws Exception {
        logger.info("User with email {} entered the librarian page. at endpoint: /users/librarian", SecurityContextHolder.getContext().getAuthentication().getName());
        List<ReservationsResponseDTO> reservations = reservationsServices.listAllPending();
        model.addAttribute("reservations", reservations);
        return "user/librarian-home";
    }

    @GetMapping("/statistics/{id}")
    private String statisticsPage(@PathVariable UUID id, Model model, @PageableDefault(size = 10) Pageable pageable) throws Exception {
        logger.info("User with email {} entered the statistics page. at endpoint: /users/statistics", SecurityContextHolder.getContext().getAuthentication().getName());
        addStatistics(model, id, pageable);
        return "user/user-home";
    }
    @GetMapping("/statisticsOfOther/{id}")
    private String statisticsOfOtherPage(@PathVariable UUID id, Model model, @PageableDefault(size = 10) Pageable pageable) throws Exception {
        logger.info("User with email {} entered the statistics page of other profile. at endpoint: /users/statistics", SecurityContextHolder.getContext().getAuthentication().getName());
        addStatistics(model, id, pageable);
        return "user/profile";
    }

    private void addStatistics(Model model, UUID id, Pageable pageable) throws Exception {
        Page<UserStatisticsBookDTO> reservedBooks = userServices.getAllReservedBooksOfTheUser(id,pageable);
        Page<UserStatisticsBookDTO> loanedBooks = userServices.getAllLoanedBooksOfTheUser(id,pageable);
        Page<UserStatisticsBookDTO> readBooks = userServices.getAllReadBooksOfTheUser(id,pageable);

        UserDTO responseDTO = userServices.getUserDTOById(id);
        model.addAttribute("user", responseDTO);

        model.addAttribute("reservedBooks", reservedBooks);
        model.addAttribute("loanedBooks", loanedBooks);
        model.addAttribute("readBooks", readBooks);
        model.addAttribute("showStatistics", true);
    }

    @GetMapping("/top")
    private String topUsers(Model model, @PageableDefault(size = 10) Pageable pageable){
        List<UserDTO> users = userServices.lisTopUsers(pageable);
        model.addAttribute("users", users);
        return "user/top-users";
    }

//    @PostMapping("/upload")
//    private String upload(@RequestParam("image") MultipartFile file, @ModelAttribute("user") UserDTO user,  Model model){
//        try{
//            String imageUrl = imageUploadService.uploadFile(file);
//            model.addAttribute("imageUrl", imageUrl);
//            model.addAttribute("user", user);
//        }catch (Exception e){
//            model.addAttribute("errorMessage", e.getMessage());
//
//        }
//        return "user/user-edit";
//    }


    @GetMapping("/all")
    private String allUsers(Model model, @PageableDefault(size = 10) Pageable pageable){
        Page<User> allUsersDefault;
        try{
            allUsersDefault = userServices.getAllUsersByRole("DEFAULT", pageable);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/admin-home";
        }
        model.addAttribute("users", allUsersDefault);
        return "user/all-users.html";
    }


}
