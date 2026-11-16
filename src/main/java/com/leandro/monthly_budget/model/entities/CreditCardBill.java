package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.CreditCardBillStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardBill extends EntityBase {
	private CreditCard creditcard;
	private LocalDate dueDate;
	private BigDecimal amount;
	private List<CreditCardTransaction> transactions;
	private List<CreditCardSubscriptions> subscriptions;
	private boolean active = true;
	private CreditCardBillStatus status;
}