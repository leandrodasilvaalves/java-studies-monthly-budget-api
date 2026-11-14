package com.leandro.monthly_budget.model.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ProjectType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {

	@NotBlank(message = "Name is required")
	private String name;

	@NotNull(message = "Category is required")
	private Long categoryId;

	@NotNull(message = "Subcategory is required")
	private Long subcategoryId;

	@NotEmpty(message = "At least one person is required")
	private List<@NotNull(message = "Person is required") Long> personIds;

	@NotNull(message = "Account is required")
	private Long accountId;

	@NotNull(message = "First deposit date is required")
	private LocalDate firstDepositDate;

	@NotNull(message = "Goal date is required")
	private LocalDate goalDate;

	@NotNull(message = "Planned amount is required")
	private BigDecimal plannedAmount;

	@NotNull(message = "Type is required")
	private ProjectType type;
}
