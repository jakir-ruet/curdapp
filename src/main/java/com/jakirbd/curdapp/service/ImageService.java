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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    public ImageResponse addImageToProduct(ImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found with id: " + request.getProductId()
            ));

        if (request.getIsPrimary()) {
            List<Image> existingImages = imageRepository.findByProductId(product.getId());
            existingImages.forEach(img -> img.setIsPrimary(false));
            imageRepository.saveAll(existingImages);
        }

        Image image = new Image();
        image.setImageUrl(request.getImageUrl());
        image.setIsPrimary(request.getIsPrimary());
        image.setProduct(product);

        Image saved = imageRepository.save(image);
        return mapToResponse(saved);
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
                "No primary image found for product: " + productId
            ));
        return mapToResponse(image);
    }

    public void deleteImage(Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image not found with id: " + imageId);
        }
        imageRepository.deleteById(imageId);
    }

    public ImageResponse setAsPrimaryImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        List<Image> productImages = imageRepository.findByProductId(image.getProduct().getId());
        productImages.forEach(img -> img.setIsPrimary(false));
        imageRepository.saveAll(productImages);

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
