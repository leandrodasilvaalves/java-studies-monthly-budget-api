package com.leandro.monthly_budget.model.services.interfaces;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardTransactionRequest;
import com.leandro.monthly_budget.model.entities.CreditCardTransaction;

public interface CreditCardTransactionService {
	
	CreditCardTransaction create(CreditCardTransactionRequest request);
}
