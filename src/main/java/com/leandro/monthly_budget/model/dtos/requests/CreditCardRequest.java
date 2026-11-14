package com.leandro.monthly_budget.model.dtos.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreditCardRequest {

	@NotBlank(message = "Name is required")	
	private String name;

	@NotNull(message = "Due day is required")
	@Min(1)
	@Max(31)
	private int dueDay;
}
