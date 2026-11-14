package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.FixedIncomeRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.FixedIncome;

public interface FixedIncomeService {

	Page<FixedIncome> getAll(int page, int size, StatusRequest status);
	FixedIncome getById(long id);
	FixedIncome create(FixedIncomeRequest request);
	FixedIncome update(long id, FixedIncomeRequest request);
	FixedIncome activate(long id);
	FixedIncome inactivate(long id);
}
