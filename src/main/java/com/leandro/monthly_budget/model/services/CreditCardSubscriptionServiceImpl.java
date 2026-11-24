package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardSubscriptionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCardSubscription;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CreditCardSubscriptionMapper;
import com.leandro.monthly_budget.model.repositories.CreditCardSubscriptionRepository;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardBillService;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardSubscriptionService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class CreditCardSubscriptionServiceImpl implements CreditCardSubscriptionService {

	private final CreditCardSubscriptionRepository subscriptionRepository;
	private final CreditCardSubscriptionMapper mapper;
	private final ReferenceDataValidator validator;
	private final CreditCardBillService billService;

	public CreditCardSubscriptionServiceImpl(
			CreditCardSubscriptionRepository subscriptionRepository,
			CreditCardSubscriptionMapper mapper,
			ReferenceDataValidator validator,
			CreditCardBillService billService) {
		this.subscriptionRepository = subscriptionRepository;
		this.mapper = mapper;
		this.validator = validator;
		this.billService = billService;
	}

	public Page<CreditCardSubscription> getAll(
			long creditCardId, int page, int size, StatusRequest status) {

		validator.requireCreditCard(creditCardId);

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> subscriptionRepository.findAllByCreditCardId(creditCardId, pageable);
			case ACTIVE -> subscriptionRepository.findAllByCreditCardIdAndStatus(creditCardId, true, pageable);
			case INACTIVE -> subscriptionRepository.findAllByCreditCardIdAndStatus(creditCardId, false, pageable);
		};
	}

	public CreditCardSubscription getById(long creditCardId, long id) {
		return subscriptionRepository.findByIdAndCreditCardId(id, creditCardId)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card subscription not found"));
	}

	public CreditCardSubscription create(long creditCardId, CreditCardSubscriptionRequest request) {

		validateRequest(request);

		var subscription = mapper.toEntity(request);
		subscription.setCreditCard(validator.requireCreditCard(creditCardId));
		applyRelations(subscription, request);

		var saved = subscriptionRepository.save(subscription);
		billService.syncBillsForSubscription(saved);

		return saved;
	}

	public CreditCardSubscription update(long creditCardId, long id, CreditCardSubscriptionRequest request) {

		var subscription = getById(creditCardId, id);

		validateRequest(request);

		subscription.setStore(request.getStore());
		subscription.setDescription(request.getDescription());
		subscription.setAmount(request.getAmount());
		subscription.setStartDate(request.getStartDate());
		subscription.setEndDate(request.getEndDate());

		if (request.getIgnore() != null) {
			subscription.setIgnore(request.getIgnore());
		}

		applyRelations(subscription, request);

		var saved = subscriptionRepository.save(subscription);
		billService.syncBillsForSubscription(saved);

		return saved;
	}

	public CreditCardSubscription activate(long creditCardId, long id) {

		var subscription = getById(creditCardId, id);
		subscription.setActive(true);

		var saved = subscriptionRepository.save(subscription);
		billService.syncBillsForSubscription(saved);

		return saved;
	}

	public CreditCardSubscription inactivate(long creditCardId, long id) {

		var subscription = getById(creditCardId, id);
		subscription.setActive(false);

		var saved = subscriptionRepository.save(subscription);
		billService.syncBillsForSubscription(saved);

		return saved;
	}

	private void validateRequest(CreditCardSubscriptionRequest request) {

		validator.requireRequest(request);
		validator.requireDateOrder(request.getStartDate(), request.getEndDate());
	}

	private void applyRelations(
			CreditCardSubscription subscription, CreditCardSubscriptionRequest request) {

		var category = validator.requireCategory(
				request.getCategoryId(), CategoryType.EXPENSE, "credit card subscriptions");

		subscription.setCategory(category);
		subscription.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		subscription.setPeople(validator.requirePeople(request.getPersonIds()));
		subscription.setTags(validator.requireTags(request.getTagIds()));
	}
}
