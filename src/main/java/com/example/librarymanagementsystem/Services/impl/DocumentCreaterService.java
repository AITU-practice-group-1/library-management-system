package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import com.example.librarymanagementsystem.DTOs.book.BookDTO;
import com.example.librarymanagementsystem.DTOs.book.BookResponseDTO;
import com.example.librarymanagementsystem.DTOs.book.ContractBookDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.librarymanagementsystem.DTOs.loan.LoanResponseDTO;
import com.example.librarymanagementsystem.Services.LoanServices;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentCreaterService {
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ObjectMapper objectMapper;

    private final LoanServices loanServices;

    public byte[] createDocument(UUID loanId) throws IOException, WriterException {
        UserDTO userDTO = loanServices.findUserByLoanId(loanId);
        ContractBookDTO bookDTO = loanServices.findBookByLoanId(loanId);
        LoanResponseDTO loanDTO = loanServices.findById(loanId);

        String jsonData = objectMapper.writeValueAsString(userDTO);
        jsonData += '\n' + objectMapper.writeValueAsString(bookDTO);
        jsonData += '\n' + objectMapper.writeValueAsString(loanDTO);

        Context context = new Context();
        context.setVariable("user", userDTO);
        context.setVariable("loan", loanDTO);
        context.setVariable("book", bookDTO);
        context.setVariable("now", LocalDateTime.now());
        String qrBase64 = generateQRCode(jsonData);
        context.setVariable("qrCode", qrBase64);
        String htmltemplate = templateEngine.process("documents/contract", context);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        String baseUrl = new File("src/main/resources/templates").toURI().toString();
        builder.withHtmlContent(htmltemplate, baseUrl);
        builder.toStream(outputStream);
        builder.run();
        return outputStream.toByteArray();
    }


    public String generateQRCode(String text) throws IOException, WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 150, 150);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
