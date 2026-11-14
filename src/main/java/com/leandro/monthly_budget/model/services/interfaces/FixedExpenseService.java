package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.FixedExpenseRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.FixedExpense;

public interface FixedExpenseService {

	Page<FixedExpense> getAll(int page, int size, StatusRequest status);
	FixedExpense getById(long id);
	FixedExpense create(FixedExpenseRequest request);
	FixedExpense update(long id, FixedExpenseRequest request);
	FixedExpense activate(long id);
	FixedExpense inactivate(long id);
}
