package com.jakirbd.curdapp.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
	private Long id;
	private String imageUrl;
	private Boolean isPrimary;
	private Long productId;
	private LocalDateTime createdAt;
}
