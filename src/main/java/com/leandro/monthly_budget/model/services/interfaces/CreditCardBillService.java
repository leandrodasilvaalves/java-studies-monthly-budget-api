package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.entities.CreditCardBill;

public interface CreditCardBillService {

	Page<CreditCardBill> getAll(long creditCardId, int page, int size);

	CreditCardBill getById(long creditCardId, long id);
}
