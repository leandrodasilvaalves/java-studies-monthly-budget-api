package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
abstract class Transaction extends EntityBase {
	private LocalDate date;
	private Category category;
	private Subcategory subcategory;
	private List<Tag> tags;	
	private String description;
	private BigDecimal amount;
	private TransactionType type;	
	private Account account;
	private LocalDate paymentDate;
	private boolean ignore = false;
	private boolean active = true;
	private String notes;
}















