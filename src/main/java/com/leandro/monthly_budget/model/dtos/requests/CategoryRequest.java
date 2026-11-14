package com.leandro.monthly_budget.model.dtos.requests;

import com.leandro.monthly_budget.model.enums.CategoryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
	
	@NotBlank(message = "Name is required")	
	private String name;
	
	@NotNull(message="Type is required")
	private CategoryType type;
}
