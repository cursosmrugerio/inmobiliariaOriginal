package com.inmobiliaria.documento.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.storage.location:./uploads}")
    private String storageLocation;

    public String storeFile(MultipartFile file, Long empresaId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;
        String relativePath = empresaId + "/" + filename;

        Path targetLocation = Paths.get(storageLocation, relativePath);
        Files.createDirectories(targetLocation.getParent());

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Archivo almacenado: {}", relativePath);
        return relativePath;
    }

    public byte[] loadFile(String relativePath) throws IOException {
        Path filePath = Paths.get(storageLocation, relativePath);
        if (!Files.exists(filePath)) {
            throw new IOException("Archivo no encontrado: " + relativePath);
        }
        return Files.readAllBytes(filePath);
    }

    public void deleteFile(String relativePath) throws IOException {
        Path filePath = Paths.get(storageLocation, relativePath);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Archivo eliminado: {}", relativePath);
        }
    }

    public boolean fileExists(String relativePath) {
        Path filePath = Paths.get(storageLocation, relativePath);
        return Files.exists(filePath);
    }
}
