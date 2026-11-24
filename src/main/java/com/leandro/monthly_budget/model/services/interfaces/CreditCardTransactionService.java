package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCardTransaction;

public interface CreditCardTransactionService {

	Page<CreditCardTransaction> getAll(long creditCardId, int page, int size, StatusRequest status);

	CreditCardTransaction getById(long creditCardId, long id);

	CreditCardTransaction create(long creditCardId, CreditCardTransactionRequest request);

	CreditCardTransaction update(long creditCardId, long id, CreditCardTransactionRequest request);

	CreditCardTransaction activate(long creditCardId, long id);

	CreditCardTransaction inactivate(long creditCardId, long id);
}
