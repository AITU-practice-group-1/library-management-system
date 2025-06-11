package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.LoanDTO;
import com.example.librarymanagementsystem.DTOs.LoanUpdateDTO;
import com.example.librarymanagementsystem.Repositories.LoanRepository;
import com.example.librarymanagementsystem.Services.LoanServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanServices loanServices;

    public String getAllLoans(Model model) {
        List<LoanDTO> loans = loanServices.findAll();
        model.addAttribute("loans", loans);
        return "loan/list";
    }

    @GetMapping("/create")
    public String createLoan(Model model) {
        model.addAttribute("loan", new LoanDTO());
        return "loan/create";
    }

    @PostMapping
    public String saveLoan(@ModelAttribute("loan") LoanDTO loanDTO) {
        loanServices.createLoan(loanDTO);
        return "redirect:/loans";
    }

    @GetMapping("/{id}")
    public String findLoanById(@PathVariable UUID id, Model model) {
        model.addAttribute("loan", loanServices.findById(id));
        return "loan/detail";
    }

    @GetMapping("/{id}/edit")
    public String updateLoan(@PathVariable UUID id, Model model) {
        model.addAttribute("loan", loanServices.findById(id));
        model.addAttribute("updateDTO", new LoanDTO());
        return "loan/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateLoan(@PathVariable UUID id, @ModelAttribute("updateDTO") LoanUpdateDTO updateDTO) {
        loanServices.updateLoan(id, updateDTO);
        return "redirect:/loans";
    }






}
