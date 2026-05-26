package com.jakirbd.curdapp.controller;

import com.jakirbd.curdapp.dto.request.CategoryRequest;
import com.jakirbd.curdapp.dto.response.CategoryResponse;
import com.jakirbd.curdapp.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	// CREATE - POST
	@PostMapping
	public ResponseEntity<CategoryResponse> createCategory(
			@Valid @RequestBody CategoryRequest request) {
		CategoryResponse response = categoryService.createCategory(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// READ ALL - GET
	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

	// READ ONE - GET
	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}

	// UPDATE - PUT
	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponse> updateCategory(
			@PathVariable Long id,
			@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}

	// DELETE - DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
