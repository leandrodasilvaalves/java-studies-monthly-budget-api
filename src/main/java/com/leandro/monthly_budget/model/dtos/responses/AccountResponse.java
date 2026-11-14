package com.leandro.monthly_budget.model.dtos.responses;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountResponse {
	private Long id;
	private String name;
	private BigDecimal balance;	
	private boolean active;
}
