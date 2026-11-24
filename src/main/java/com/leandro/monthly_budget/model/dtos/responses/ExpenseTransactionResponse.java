package com.leandro.monthly_budget.model.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ExpenseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseTransactionResponse {

	private Long id;
	private CategoryResponse category;
	private SubcategoryResponse subcategory;
	private String store;
	private List<PersonResponse> people;
	private String description;
	private BigDecimal amount;
	private boolean active;
	private List<TagResponse> tags;
	private LocalDate registeredDate;
	private boolean ignore;
	private String notes;
	private ExpenseStatus status;
	private AccountResponse account;
	private LocalDate paidDate;
}
