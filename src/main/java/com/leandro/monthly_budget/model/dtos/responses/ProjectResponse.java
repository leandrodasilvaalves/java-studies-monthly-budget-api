package com.leandro.monthly_budget.model.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ProjectType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {

	private Long id;
	private String name;
	private CategoryResponse category;
	private SubcategoryResponse subcategory;
	private List<PersonResponse> people;
	private AccountResponse account;
	private LocalDate firstDepositDate;
	private LocalDate goalDate;
	private BigDecimal plannedAmount;
	private ProjectType type;
	private boolean active;
}
