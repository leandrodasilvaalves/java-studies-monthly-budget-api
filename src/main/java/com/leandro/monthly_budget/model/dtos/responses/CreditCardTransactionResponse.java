package com.leandro.monthly_budget.model.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.leandro.monthly_budget.model.enums.CreditCardTransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardTransactionResponse {

	private Long id;
	private Long creditCardBillId;
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
	private int totalInstallments;
	private int installmentNumber;
	private CreditCardTransactionStatus status;
	private UUID purchaseId;
	private LocalDate paidDate;
}
