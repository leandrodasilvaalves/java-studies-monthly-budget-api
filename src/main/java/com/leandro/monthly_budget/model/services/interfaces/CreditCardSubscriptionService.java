package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardSubscriptionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCardSubscription;

public interface CreditCardSubscriptionService {

	Page<CreditCardSubscription> getAll(long creditCardId, int page, int size, StatusRequest status);
	CreditCardSubscription getById(long creditCardId, long id);
	CreditCardSubscription create(long creditCardId, CreditCardSubscriptionRequest request);
	CreditCardSubscription update(long creditCardId, long id, CreditCardSubscriptionRequest request);
	CreditCardSubscription activate(long creditCardId, long id);
	CreditCardSubscription inactivate(long creditCardId, long id);
}
