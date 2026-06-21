package com.jakirbd.curdapp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {

	@Value("${upload.dir:uploads/images/}") // Using standard property with default value
	private String uploadDir;

	@PostConstruct
	public void init() {
		try {
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
				System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
			}
		} catch (Exception e) {
			System.err.println("Could not create upload directory: " + e.getMessage());
		}
	}
}
