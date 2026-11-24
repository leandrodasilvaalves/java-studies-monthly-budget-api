package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.IncomeTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.IncomeTransaction;

public interface IncomeTransactionService {

	Page<IncomeTransaction> getAll(int page, int size, StatusRequest status);
	IncomeTransaction getById(long id);
	IncomeTransaction create(IncomeTransactionRequest request);
	IncomeTransaction update(long id, IncomeTransactionRequest request);
	IncomeTransaction activate(long id);
	IncomeTransaction inactivate(long id);
}
