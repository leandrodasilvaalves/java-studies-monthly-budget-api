package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardSubscriptionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCardSubscriptions;

public interface CreditCardSubscriptionService {

	Page<CreditCardSubscriptions> getAll(long creditCardId, int page, int size, StatusRequest status);
	CreditCardSubscriptions getById(long creditCardId, long id);
	CreditCardSubscriptions create(long creditCardId, CreditCardSubscriptionRequest request);
	CreditCardSubscriptions update(long creditCardId, long id, CreditCardSubscriptionRequest request);
	CreditCardSubscriptions activate(long creditCardId, long id);
	CreditCardSubscriptions inactivate(long creditCardId, long id);
}
