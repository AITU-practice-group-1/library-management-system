package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.DTOs.loan.LoanDTO;
import com.example.librarymanagementsystem.Services.impl.DocumentCreaterService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ContractDeliveryController {
    @Autowired
    private DocumentCreaterService documentCreaterService;


    @GetMapping(value = "loans/contract/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadContract(@RequestParam UUID loanId) throws IOException, WriterException {
        byte[] pdf =  documentCreaterService.createDocument(loanId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contract.pdf").contentType(MediaType.APPLICATION_PDF).body(pdf);
    }
}