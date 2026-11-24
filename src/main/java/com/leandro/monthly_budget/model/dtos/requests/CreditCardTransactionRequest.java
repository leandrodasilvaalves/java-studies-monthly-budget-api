package com.leandro.monthly_budget.model.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.leandro.monthly_budget.model.enums.CreditCardTransactionStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardTransactionRequest {

	@NotNull(message = "Registered date is required")
	private LocalDate registeredDate;

	@NotNull(message = "Category is required")
	private Long categoryId;

	@NotNull(message = "Subcategory is required")
	private Long subcategoryId;

	private String store;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Amount is required")
	private BigDecimal amount;

	@NotNull(message = "Total installments is required")
	@Min(value = 1, message = "Total installments must be at least 1")
	private Integer totalInstallments;

	@NotNull(message = "Installment number is required")
	@Min(value = 1, message = "Installment number must be at least 1")
	private Integer installmentNumber;

	@NotNull(message = "Status is required")
	private CreditCardTransactionStatus status;

	private LocalDate paidDate;

	private UUID purchaseId;

	private List<@NotNull(message = "Person is required") Long> personIds;

	private List<@NotNull(message = "Tag is required") Long> tagIds;

	private Boolean ignore;

	private String notes;
}
