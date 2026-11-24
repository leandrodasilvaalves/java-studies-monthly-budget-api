package com.leandro.monthly_budget.model.services;

import org.springframework.stereotype.Service;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardTransactionRequest;
import com.leandro.monthly_budget.model.entities.CreditCardTransaction;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardTransactionService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CreditCardTransactionServiceImpl implements CreditCardTransactionService{

	
	public CreditCardTransaction create(CreditCardTransactionRequest request) {
		// TODO Auto-generated method stub
		return null;
	}	
	
}
