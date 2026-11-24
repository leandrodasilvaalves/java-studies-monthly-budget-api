package com.leandro.monthly_budget.model.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ProjectTransactionStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTransactionResponse {

	private Long id;
	private ProjectResponse project;
	private CategoryResponse category;
	private SubcategoryResponse subcategory;
	private LocalDate dueDate;
	private ProjectTransactionType projectTransactionType;
	private List<PersonResponse> people;
	private ProjectTransactionStatus status;
	private AccountResponse account;
	private LocalDate movementDate;
	private String description;
	private BigDecimal amount;
	private boolean active;
	private List<TagResponse> tags;
	private LocalDate registeredDate;
	private boolean ignore;
	private String notes;
}
