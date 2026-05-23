package com.jakirbd.curdapp.service;

import com.jakirbd.curdapp.model.Category;
import com.jakirbd.curdapp.model.Image;

import org.springframework.stereotype.Service;

import com.jakirbd.curdapp.dto.request.ProductRequest;
import com.jakirbd.curdapp.dto.response.ProductResponse;
import com.jakirbd.curdapp.exception.DuplicateResourceException;
import com.jakirbd.curdapp.exception.ResourceNotFoundException;
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
					"Product with name '" + request.getName() + "' already exists");
		}
		         Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Category not found with id: " + request.getCategoryId()
                ));
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
				image.setN
			});
		}
	}
}
