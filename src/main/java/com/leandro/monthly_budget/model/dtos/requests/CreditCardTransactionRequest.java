package com.leandro.monthly_budget.model.dtos.requests;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreditCardTransactionRequest {

	private Long creditCardBillId;

	private Long categoryId;

	private Long subcategoryId;

	private String store;

	private String description;

	private int totalInstallments;

	private int installmentNumber;

	private BigDecimal amount;

	private List<@NotNull(message = "Person is required") Long> personIds;

	private List<Long> tagsIds;

	private boolean ignore = false;

	private String notes;
}
