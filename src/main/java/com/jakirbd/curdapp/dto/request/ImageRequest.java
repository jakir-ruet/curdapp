package com.jakirbd.curdapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImageRequest {
	@NotBlank(message = "Image URL is required")
	private String imageUrl;
	private Boolean isPrimary = false;
	@NotNull(message = "Product ID is required")
	private Long productId;
}
