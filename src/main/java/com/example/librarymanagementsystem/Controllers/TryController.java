package com.example.librarymanagementsystem.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TryController {
    @GetMapping("/try")
    public String tryPage(){
        return "try";
    }
}
