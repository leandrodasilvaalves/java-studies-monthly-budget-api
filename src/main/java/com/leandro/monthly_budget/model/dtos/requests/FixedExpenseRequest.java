package com.leandro.monthly_budget.model.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedExpenseRequest {

	@NotNull(message = "Category is required")
	private Long categoryId;

	@NotNull(message = "Subcategory is required")
	private Long subcategoryId;

	private String store;

	private List<@NotNull(message = "Person is required") Long> personIds;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Amount is required")
	private BigDecimal amount;

	@NotNull(message = "Start date is required")
	private LocalDate startDate;

	@NotNull(message = "End date is required")
	private LocalDate endDate;

	private List<@NotNull(message = "Tag is required") Long> tagIds;

	private Boolean ignore;
}
