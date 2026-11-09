package com.leandro.monthly_budget.controller.requests;

import com.leandro.monthly_budget.model.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
	private String name;
	private CategoryType type;
}
