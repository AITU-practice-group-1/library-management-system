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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final Cloudinary cloudinary;
    private final UserServices userServices;
    public String uploadUserFile(MultipartFile file) throws IOException, Exception {
        String oldPhotoId = userServices.getAuthenticatedUser().getImageId();
        String[] result = uploadFile(file);
        String url = result[0];
        String photoId = result[1];
        UserDTO dto = new UserDTO();
        dto.setImageId(photoId);
        dto.setImageUrl(url);
        userServices.updateUser(dto);
        if(oldPhotoId != null)
        {
            cloudinary.uploader().destroy(oldPhotoId, ObjectUtils.emptyMap());
        }
        return url;
    }

    public String[] uploadBookFileWithId(MultipartFile file, String oldPhotoId) throws IOException
    {
        String[] result = uploadFile(file);
        String url = result[0];
        String photoId = result[1];
        if(oldPhotoId != null)
        {
            cloudinary.uploader().destroy(oldPhotoId, ObjectUtils.emptyMap());
        }
        return new String[] {url, photoId};
    }

    public String[] uploadFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile);
        try {
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
            String publicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("url");
            return new String[] {url, publicId};
        } finally {
            tempFile.delete();
        }
    }

}
