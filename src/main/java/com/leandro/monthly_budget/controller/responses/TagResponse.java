package com.leandro.monthly_budget.controller.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TagResponse {
	private Long id;
	private String name;
	private boolean active;
}