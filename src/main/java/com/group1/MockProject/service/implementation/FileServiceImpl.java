package com.group1.MockProject.service.implementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group1.MockProject.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private Cloudinary cloudinary;

    public FileServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFileWithCloudinary(MultipartFile multipartFile, String publicId) throws IOException {
        String fileType = multipartFile.getContentType();
        Map<String, Object> uploadParams = new HashMap<>();

        if (fileType != null) {
            if (fileType.startsWith("image")) {
                uploadParams.put("resource_type", "image");
            } else if (fileType.startsWith("video")) {
                uploadParams.put("resource_type", "video");
            } else if (fileType.equals("application/pdf")) {
                uploadParams.put("resource_type", "raw");
            } else {
                uploadParams.put("resource_type", "raw");
            }
        }
        uploadParams.put("public_id", publicId);  // Đặt public_id nếu cần

        // Upload tệp lên Cloudinary
        return cloudinary.uploader()
                .upload(multipartFile.getBytes(), uploadParams)
                .get("url").toString();
    }

    @Override
    public boolean deleteFileFromCloudinary(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return true;
        } catch (IOException e) {
            return false;
        }

    }
}
