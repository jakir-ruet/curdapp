package com.jakirbd.curdapp.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stockQuantity;
	private Long categoryId;
	private String categoryName;
	private List<ImageResponse> images;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
}
