package com.jakirbd.curdapp.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
	private Long id;
	private String name;
	private String description;
	private Integer production;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
}
