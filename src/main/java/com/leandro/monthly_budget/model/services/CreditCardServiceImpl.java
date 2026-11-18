package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CreditCardMapper;
import com.leandro.monthly_budget.model.repositories.CreditCardRepository;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardService;

@Service
@Transactional
public class CreditCardServiceImpl implements CreditCardService {

	private final CreditCardRepository creditCardRepository;
	private final CreditCardMapper mapper;

	public CreditCardServiceImpl(CreditCardRepository creditCardRepository, CreditCardMapper mapper) {
		this.creditCardRepository = creditCardRepository;
		this.mapper = mapper;
	}

	public Page<CreditCard> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return status == StatusRequest.ALL
				? creditCardRepository.findAll(pageable)
				: creditCardRepository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
	}

	public CreditCard getById(long id) {
		return creditCardRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
	}

	public CreditCard create(CreditCardRequest request) {

		if (creditCardRepository.existsByName(request.getName())) {
			throw new DuplicateResourceException("Already exists a credit card with this name");
		}

		return creditCardRepository.save(mapper.toEntity(request));
	}

	public CreditCard update(long id, CreditCardRequest request) {

		var creditCard = getById(id);
		creditCard.setName(request.getName());
		creditCard.setDueDay(request.getDueDay());

		return creditCardRepository.save(creditCard);
	}

	public CreditCard activate(long id) {

		var creditCard = getById(id);
		creditCard.setActive(true);

		return creditCardRepository.save(creditCard);
	}

	public CreditCard inactivate(long id) {

		var creditCard = getById(id);
		creditCard.setActive(false);

		return creditCardRepository.save(creditCard);
	}
}
