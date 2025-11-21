package com.inmobiliaria.documento.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class FileStorageConfig {

    @Value("${app.file-storage.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.file-storage.max-file-size:10485760}")
    private long maxFileSize; // 10MB default
}
