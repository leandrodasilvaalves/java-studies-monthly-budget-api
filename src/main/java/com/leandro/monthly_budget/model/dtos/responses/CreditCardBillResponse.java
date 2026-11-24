package com.leandro.monthly_budget.model.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.leandro.monthly_budget.model.enums.CreditCardBillStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardBillResponse {

	private Long id;
	private CreditCardResponse creditCard;
	private LocalDate dueDate;
	private CreditCardBillStatus status;
	private LocalDate paidDate;
	private AccountResponse paidFromAccount;
	private BigDecimal amount;
	private boolean hasEntries;
}
