package com.jakirbd.curdapp.service;

import com.jakirbd.curdapp.dto.request.CategoryRequest;
import com.jakirbd.curdapp.dto.response.CategoryResponse;
import com.jakirbd.curdapp.exception.BusinessException;
import com.jakirbd.curdapp.exception.DuplicateResourceException;
import com.jakirbd.curdapp.exception.ResourceNotFoundException;
import com.jakirbd.curdapp.model.Category;
import com.jakirbd.curdapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional

public class CategoryService {

	private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                "Category with name '" + request.getName() + "' already exists"
            );
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

		public CategoryResponse getCategoryById(Long id) {
			Category category = categoryRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(
							"Category not found with id: " + id));
			return mapToResponse(category);
		}

		public CategoryResponse updateCategory(Long id, CategoryRequest request) {
			Category category = categoryRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(
							"Category not found with id: " + id));

			category.setName(request.getName());
			category.setDescription(request.getDescription());

			Category updated = categoryRepository.save(category);
			return mapToResponse(updated);
		}

		public void deleteCategory(Long id) {
			Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
					"Category not found with id " + id));
			if (!category.getProducts().isEmpty()) {
				throw new BusinessException("Can't delete category with associated products, Delete the products first"

				);
			}
			categoryRepository.delete(category);
		}

		private CategoryResponse mapToResponse(Category category) {
		return CategoryResponse.builder()
		.id(category.getId())
		.name(category.getName())
		.description(category.getDescription())
		.createAt(category.getCreatedAt())
		.updateAt(category.getUpdatedAt())
		.build();

	}
}
