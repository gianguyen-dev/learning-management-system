package com.group1.MockProject.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFileWithCloudinary(MultipartFile multipartFile, String publicId) throws IOException;
    boolean deleteFileFromCloudinary(String publicId);
}
