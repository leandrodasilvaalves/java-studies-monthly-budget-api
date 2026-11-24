package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.ExpenseTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.ExpenseTransaction;

public interface ExpenseTransactionService {

	Page<ExpenseTransaction> getAll(int page, int size, StatusRequest status);
	ExpenseTransaction getById(long id);
	ExpenseTransaction create(ExpenseTransactionRequest request);
	ExpenseTransaction update(long id, ExpenseTransactionRequest request);
	ExpenseTransaction activate(long id);
	ExpenseTransaction inactivate(long id);
}
