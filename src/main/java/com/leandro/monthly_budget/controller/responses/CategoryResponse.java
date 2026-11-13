package com.leandro.monthly_budget.controller.responses;

import com.leandro.monthly_budget.model.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
	private Long id;
	private String name;
	private CategoryType type;
	private boolean active;
}
