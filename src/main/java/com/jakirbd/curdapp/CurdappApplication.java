package com.jakirbd.curdapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CurdappApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurdappApplication.class, args);
        System.out.println("=".repeat(70));
        System.out.println("🚀 Product Management CRUD Application");
        System.out.println("📍 Application started successfully!");
        System.out.println("📝 API Base URL: http://localhost:8090/api");
        System.out.println("📚 API Endpoints:");
        System.out.println("   - Categories:  /api/categories");
        System.out.println("   - Products:    /api/products");
        System.out.println("   - Images:      /api/images");
        System.out.println("=".repeat(70));
    }
}
