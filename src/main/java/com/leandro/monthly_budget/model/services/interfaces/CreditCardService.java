package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCard;

public interface CreditCardService {

	Page<CreditCard> getAll(int page, int size, StatusRequest status);
	CreditCard getById(long id);
	CreditCard create(CreditCardRequest request);
	CreditCard update(long id, CreditCardRequest request);
	CreditCard activate(long id);
	CreditCard inactivate(long id);
}
