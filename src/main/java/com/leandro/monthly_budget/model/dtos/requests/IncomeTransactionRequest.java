package com.leandro.monthly_budget.model.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.IncomeStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeTransactionRequest {

	@NotNull(message = "Registered date is required")
	private LocalDate registeredDate;

	@NotNull(message = "Category is required")
	private Long categoryId;

	@NotNull(message = "Subcategory is required")
	private Long subcategoryId;

	private String payer;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Amount is required")
	private BigDecimal amount;

	@NotNull(message = "Status is required")
	private IncomeStatus status;

	private Long accountId;

	private LocalDate receivedDate;

	private List<@NotNull(message = "Tag is required") Long> tagIds;

	private Boolean ignore;

	private String notes;
}
