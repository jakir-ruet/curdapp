package com.jakirbd.curdapp.dto.request;

import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {
	@NotBlank(message = "Product name is required")
	@Size(min = 2, max = 200, message = "Product name must be between 2 and 200 chanracters")
	private String name;

	@Size(max = 1000, message = "Description can't exceed 1000 characters")
	private String description;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be positive")
	@DecimalMin(value = "0.01", message = "Price must be at least 999999.99")
	@DecimalMax(value = "999999.99", message = "Price can't execeed 1,000.000")
	private Integer stockQuantity = 0;
	private Long catagoryId;
	private List<ImageRequest> images;
}
