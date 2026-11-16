package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardTransaction extends ExpenseTransaction {
	private CreditCardBill creditcardBill;
	private int totalInstallments;
	private int installmentNumber;
	private BigDecimal installAmount;
}