package com.jakirbd.curdapp.controller;

import com.jakirbd.curdapp.dto.request.ImageRequest;
import com.jakirbd.curdapp.dto.response.ImageResponse;
import com.jakirbd.curdapp.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
	private final ImageService imageService;

	// For web URLs (JSON)
	@PostMapping
	public ResponseEntity<ImageResponse> addImageFromUrl(
			@Valid @RequestBody ImageRequest request) {
		ImageResponse response = imageService.addImageToProduct(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// For local file uploads (multipart/form-data)
	@PostMapping("/upload")
	public ResponseEntity<ImageResponse> uploadImage(
			@RequestParam("file") MultipartFile file,
			@RequestParam("isPrimary") Boolean isPrimary,
			@RequestParam("productId") Long productId) {
		ImageResponse response = imageService.uploadImageToProduct(file, isPrimary, productId);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<List<ImageResponse>> getProductImages(@PathVariable Long productId) {
		return ResponseEntity.ok(imageService.getImagesByProduct(productId));
	}

	@GetMapping("/product/{productId}/primary")
	public ResponseEntity<ImageResponse> getPrimaryImage(@PathVariable Long productId) {
		return ResponseEntity.ok(imageService.getPrimaryImage(productId));
	}

	@PutMapping("/{imageId}/primary")
	public ResponseEntity<ImageResponse> setPrimaryImage(@PathVariable Long imageId) {
		return ResponseEntity.ok(imageService.setAsPrimaryImage(imageId));
	}

	@DeleteMapping("/{imageId}")
	public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
		imageService.deleteImage(imageId);
		return ResponseEntity.noContent().build();
	}
}
