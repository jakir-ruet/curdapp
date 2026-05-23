package com.jakirbd.curdapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakirbd.curdapp.dto.request.CategoryRequest;
import com.jakirbd.curdapp.dto.response.CategoryResponse;
import com.jakirbd.curdapp.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor

public class CategoryController {
	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryResponse> createCategory(
			@Valid @RequestBody CategoryRequest request) {
		CategoryResponse response = categoryService.createCategory(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponse> updateCategory(
		@PathVariable Long id,
			@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
