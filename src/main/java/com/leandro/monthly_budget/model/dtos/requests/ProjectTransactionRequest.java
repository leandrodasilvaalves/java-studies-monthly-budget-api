package com.leandro.monthly_budget.model.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ProjectTransactionStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTransactionRequest {

	@NotNull(message = "Registered date is required")
	private LocalDate registeredDate;

	@NotNull(message = "Category is required")
	private Long categoryId;

	@NotNull(message = "Subcategory is required")
	private Long subcategoryId;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Amount is required")
	private BigDecimal amount;

	@NotNull(message = "Project transaction type is required")
	private ProjectTransactionType projectTransactionType;

	@NotNull(message = "Status is required")
	private ProjectTransactionStatus status;

	private LocalDate dueDate;

	private LocalDate movementDate;

	private Long accountId;

	private List<@NotNull(message = "Person is required") Long> personIds;

	private List<@NotNull(message = "Tag is required") Long> tagIds;

	private Boolean ignore;

	private String notes;
}
