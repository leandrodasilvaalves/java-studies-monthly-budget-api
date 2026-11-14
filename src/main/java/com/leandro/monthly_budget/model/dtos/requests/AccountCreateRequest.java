package com.leandro.monthly_budget.model.dtos.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountCreateRequest {
	
	@NotBlank(message = "Name is required")
	private String name;
	
	@NotNull(message = "Balance is required")
	private BigDecimal balance;
}