package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account extends EntityBase {

	public Account(String name, BigDecimal balance) {
		super();
		this.name = name;
		this.balance = balance;
	}

	@Column(nullable = false, length = 80, unique = true)
	private String name;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal balance;

	private boolean active = true;
}
