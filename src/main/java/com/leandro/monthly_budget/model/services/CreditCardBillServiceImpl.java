package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.entities.CreditCardBill;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.repositories.CreditCardBillRepository;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardBillService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional(readOnly = true)
public class CreditCardBillServiceImpl implements CreditCardBillService {

	private final CreditCardBillRepository billRepository;
	private final ReferenceDataValidator validator;

	public CreditCardBillServiceImpl(
			CreditCardBillRepository billRepository, ReferenceDataValidator validator) {
		this.billRepository = billRepository;
		this.validator = validator;
	}

	public Page<CreditCardBill> getAll(long creditCardId, int page, int size) {

		validator.requireCreditCard(creditCardId);

		Pageable pageable = PageRequest.of(page - 1, size);

		return billRepository.findAllByCreditCardId(creditCardId, pageable);
	}

	public CreditCardBill getById(long creditCardId, long id) {
		return billRepository.findByIdAndCreditCardId(id, creditCardId)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card bill not found"));
	}
}
