package com.leandro.monthly_budget.model.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.IncomeStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeTransactionResponse {

	private Long id;
	private CategoryResponse category;
	private SubcategoryResponse subcategory;
	private String payer;
	private String description;
	private BigDecimal amount;
	private boolean active;
	private List<TagResponse> tags;
	private LocalDate registeredDate;
	private boolean ignore;
	private String notes;
	private IncomeStatus status;
	private AccountResponse account;
	private LocalDate receivedDate;
}
