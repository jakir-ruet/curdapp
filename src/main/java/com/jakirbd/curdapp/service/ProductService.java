package com.jakirbd.curdapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakirbd.curdapp.dto.request.ProductRequest;
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

	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				"Product not found with id " + id));
		return mapToResponse(product);
	}

	public List<ProductResponse> getProductByCategory(Long categoryId) {
		List<Product> products = new productRepository.findByCategoryId(categoryId);

		return products.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public ProductResponse updateProduct(Long id, ProductRequest request) {
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
			"Product not found with id " + id
		));

		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());

		if (request.getCatagoryId() != null) {
			Category category = categoryRepository.findById(request.getCatagoryId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Category not found with id " + request.getCatagoryId()
					));
			product.setCategory(category);
		} else {
			product.setCategory(null);
		}
		Product updated = productRepository.save(product);
		return mapToResponse(updated);
	}
}
