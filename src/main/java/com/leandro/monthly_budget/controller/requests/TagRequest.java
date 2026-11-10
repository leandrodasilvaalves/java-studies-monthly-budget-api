package com.leandro.monthly_budget.controller.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TagRequest {		
	
	@NotBlank(message = "Name is required")
	private String name;	
}
