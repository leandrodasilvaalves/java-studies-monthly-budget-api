package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.AccountCreateRequest;
import com.leandro.monthly_budget.model.dtos.requests.AccountUpdateRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Account;

public interface AccountService {

	Page<Account> getAll(int page, int size, StatusRequest status);
	Account getById(long id);
	Account create(AccountCreateRequest request);
	Account update(long id, AccountUpdateRequest request);
	Account activate(long id);
	Account inactivate(long id);
}
