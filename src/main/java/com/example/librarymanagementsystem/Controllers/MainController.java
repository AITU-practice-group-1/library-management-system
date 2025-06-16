package com.example.librarymanagementsystem.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/error")
    public String error(){
        return "error";
    }
    @GetMapping("/error/403")
    public String error403(){
        return "access-denied";
    }
}
