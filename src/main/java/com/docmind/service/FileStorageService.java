package com.docmind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + File.separator + "uploads";

    private String uploadedFilePath;

    public String saveFile(MultipartFile file) throws IOException {

        File directory = new File(UPLOAD_DIR);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File destination = new File(directory, file.getOriginalFilename());

        file.transferTo(destination);

        uploadedFilePath = destination.getAbsolutePath();

        return uploadedFilePath;
    }

    public String getUploadedFilePath() {
        return uploadedFilePath;
    }
}