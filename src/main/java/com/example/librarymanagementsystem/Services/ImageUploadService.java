package com.example.librarymanagementsystem.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.librarymanagementsystem.DTOs.Users.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final Cloudinary cloudinary;
    private final UserServices userServices;
    public String uploadFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile);
        try {
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
            String url = (String) uploadResult.get("url");
            UserDTO dto = new UserDTO();
            dto.setImageUrl(url);
            userServices.updateUser(dto);
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();
        }
    }
}
