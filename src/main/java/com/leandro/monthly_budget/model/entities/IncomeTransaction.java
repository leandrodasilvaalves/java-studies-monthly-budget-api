package com.leandro.monthly_budget.model.entities;

import com.leandro.monthly_budget.model.enums.IncomeStatus;

import lombok.Data;

@Data
public class IncomeTransaction extends Transaction{
	private String payer;
	private Person receiver;
	private IncomeStatus status;
}