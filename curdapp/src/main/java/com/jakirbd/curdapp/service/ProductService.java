package com.jakirbd.curdapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jakirbd.curdapp.dto.request.ProductRequest;
import com.jakirbd.curdapp.dto.response.ImageResponse;
import com.jakirbd.curdapp.dto.response.ProductResponse;
import com.jakirbd.curdapp.exception.DuplicateResourceException;
import com.jakirbd.curdapp.exception.ResourceNotFoundException;
import com.jakirbd.curdapp.model.Category;
import com.jakirbd.curdapp.model.Image;
import com.jakirbd.curdapp.model.Product;
import com.jakirbd.curdapp.repository.CategoryRepository;
import com.jakirbd.curdapp.repository.ImageRepository;
import com.jakirbd.curdapp.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional

public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ImageRepository imageRepository;

	public ProductResponse createProduct(ProductRequest request) {
		if (productRepository.existsByName(request.getName())) {
			throw new DuplicateResourceException(
					"Product with name " + request.getName() + "already exists");
		}
		Category category = null;

		if (request.getCatagoryId() != null) {
			category = categoryRepository.findById(request.getCatagoryId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Category not found wit id " + request.getCatagoryId()));
		}

		Product product = new Product();

		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());
		product.setCategory(category);

		Product saved = productRepository.save(product);

		if (request.getImages() != null && !request.getImages().isEmpty()) {
			request.getImages().forEach(imgReq -> {
				Image image = new Image();
				image.setImageUrl(imgReq.getImageUrl());
				image.setIsPrimary(imgReq.getIsPrimary());
				image.setProduct(saved);
				imageRepository.save(image);
			});
		}
		return mapToResponse(saved);
	}

	public List<ProductResponse> getAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Product> productPage = productRepository.findAll(pageable);
		return productPage.getContent().stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				"Product not found with id " + id));
		return mapToResponse(product);
	}

	public List<ProductResponse> getProductsByCategory(Long categoryId) {
		List<Product> products = productRepository.findByCategoryId(categoryId);

		return products.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public ProductResponse updateProduct(Long id, ProductRequest request) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				"Product not found with id " + id));

		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());

		if (request.getCatagoryId() != null) {
			Long categoryId = request.getCatagoryId();
			Category category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new ResourceNotFoundException(
							"Category not found with id " + categoryId));
			product.setCategory(category);
		} else {
			product.setCategory(null);
		}
		Product updated = productRepository.save(product);
		return mapToResponse(updated);
	}

	public void deleteProduct(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("Product ID cannot be null");
		}
		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Product not found with id " + id);
		}
		imageRepository.deleteByProductId(id);
		productRepository.deleteById(id);
	}
	private ProductResponse mapToResponse(Product product) {
		List<ImageResponse> imageResponses = imageRepository.findByProductId(product.getId()).stream()
				.map(img -> ImageResponse.builder()
					.id(img.getId())
					.imageUrl(img.getImageUrl())
					.isPrimary(img.getIsPrimary())
					.productId(img.getProduct().getId())
					.createdAt(img.getCreatedAt())
					.build())
				.collect(Collectors.toList());

		return ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.stockQuantity(product.getStockQuantity())
				.categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
				.categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
				.createdDate(product.getCreatedAt())
				.updatedDate(product.getUpdatedAt())
				.images(imageResponses)
				.build();
	}
}
