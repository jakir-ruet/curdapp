package com.jakirbd.curdapp.service;

import com.jakirbd.curdapp.dto.request.ImageRequest;
import com.jakirbd.curdapp.dto.response.ImageResponse;
import com.jakirbd.curdapp.exception.ResourceNotFoundException;
import com.jakirbd.curdapp.model.Image;
import com.jakirbd.curdapp.model.Product;
import com.jakirbd.curdapp.repository.ImageRepository;
import com.jakirbd.curdapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    // Method for web URLs (JSON)
    public ImageResponse addImageToProduct(ImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        if (request.getIsPrimary()) {
            List<Image> existingImages = imageRepository.findByProductId(product.getId());
            existingImages.forEach(img -> img.setIsPrimary(false));
            imageRepository.saveAll(existingImages);
        }

        Image image = new Image();
        image.setImageUrl(request.getImageUrl());
        image.setIsPrimary(request.getIsPrimary());
        image.setProduct(product);
        image.setCreatedAt(LocalDateTime.now());

        Image saved = imageRepository.save(image);
        return mapToResponse(saved);
    }

    // Method for local file uploads
    public ImageResponse uploadImageToProduct(MultipartFile file, Boolean isPrimary, Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + productId));

            // Create upload directory if not exists
            String uploadDir = "uploads/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // If this image is set as primary, update other images
            if (isPrimary) {
                List<Image> existingImages = imageRepository.findByProductId(product.getId());
                existingImages.forEach(img -> img.setIsPrimary(false));
                imageRepository.saveAll(existingImages);
            }

            // Create image record
            Image image = new Image();
            image.setImageUrl("/uploads/images/" + filename);
            image.setIsPrimary(isPrimary);
            image.setProduct(product);
            image.setCreatedAt(LocalDateTime.now());

            Image saved = imageRepository.save(image);
            return mapToResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    public List<ImageResponse> getImagesByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        List<Image> images = imageRepository.findByProductId(productId);
        return images.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ImageResponse getPrimaryImage(Long productId) {
        Image image = imageRepository.findByProductIdAndIsPrimaryTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No primary image found for product: " + productId));
        return mapToResponse(image);
    }

    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        // Delete the physical file
        String imageUrl = image.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
            try {
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get("uploads/images/").resolve(filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete image file: " + e.getMessage());
            }
        }

        imageRepository.deleteById(imageId);
    }

    public ImageResponse setAsPrimaryImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        // Set all images of the same product to non-primary
        List<Image> productImages = imageRepository.findByProductId(image.getProduct().getId());
        productImages.forEach(img -> img.setIsPrimary(false));
        imageRepository.saveAll(productImages);

        // Set this image as primary
        image.setIsPrimary(true);
        Image updated = imageRepository.save(image);

        return mapToResponse(updated);
    }

    private ImageResponse mapToResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .productId(image.getProduct().getId())
                .createdAt(image.getCreatedAt())
                .build();
    }
}
