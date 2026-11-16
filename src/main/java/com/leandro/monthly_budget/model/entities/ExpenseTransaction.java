package com.leandro.monthly_budget.model.entities;

import java.util.List;

import com.leandro.monthly_budget.model.enums.ExpenseStatus;

import lombok.Data;

@Data
public class ExpenseTransaction extends Transaction{
	private String store;
	private List<Person> people;
	private ExpenseStatus status;
}