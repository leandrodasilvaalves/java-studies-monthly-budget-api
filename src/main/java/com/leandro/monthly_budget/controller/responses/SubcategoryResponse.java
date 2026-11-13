package com.leandro.monthly_budget.controller.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubcategoryResponse {
	private Long id;
	private String name;
	private CategoryResponse category;
	private boolean active;
}
