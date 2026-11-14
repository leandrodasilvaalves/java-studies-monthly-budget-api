package com.leandro.monthly_budget.model.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardResponse {
	
	private Long id;
	private String name;
	private int dueDay;
	private boolean active;
}
