package com.leandro.monthly_budget.model.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonResponse {
	private Long id;
	private String name;
	private boolean active;
}