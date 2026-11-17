package com.leandro.monthly_budget.model.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardSubscriptionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.entities.CreditCardSubscriptions;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CreditCardSubscriptionMapper;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.CreditCardRepository;
import com.leandro.monthly_budget.model.repositories.CreditCardSubscriptionRepository;
import com.leandro.monthly_budget.model.repositories.PersonRepository;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;
import com.leandro.monthly_budget.model.repositories.TagRepository;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardSubscriptionService;

@Service
public class CreditCardSubscriptionServiceImpl implements CreditCardSubscriptionService {

	private CreditCardSubscriptionRepository subscriptionRepository;
	private CreditCardRepository creditCardRepository;
	private CategoryRepository categoryRepository;
	private SubcategoryRepository subcategoryRepository;
	private PersonRepository personRepository;
	private TagRepository tagRepository;
	private CreditCardSubscriptionMapper mapper;

	public CreditCardSubscriptionServiceImpl(
			CreditCardSubscriptionRepository subscriptionRepository,
			CreditCardRepository creditCardRepository,
			CategoryRepository categoryRepository,
			SubcategoryRepository subcategoryRepository,
			PersonRepository personRepository,
			TagRepository tagRepository,
			CreditCardSubscriptionMapper mapper) {
		super();
		this.subscriptionRepository = subscriptionRepository;
		this.creditCardRepository = creditCardRepository;
		this.categoryRepository = categoryRepository;
		this.subcategoryRepository = subcategoryRepository;
		this.personRepository = personRepository;
		this.tagRepository = tagRepository;
		this.mapper = mapper;
	}

	public Page<CreditCardSubscriptions> getAll(
			long creditCardId, int page, int size, StatusRequest status) {

		findCreditCard(creditCardId);

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> subscriptionRepository.findAllByCreditCardId(creditCardId, pageable);
			case ACTIVE -> subscriptionRepository.findAllByCreditCardIdAndStatus(creditCardId, true, pageable);
			case INACTIVE -> subscriptionRepository.findAllByCreditCardIdAndStatus(creditCardId, false, pageable);
		};
	}

	public CreditCardSubscriptions getById(long creditCardId, long id) {
		return subscriptionRepository.findByIdAndCreditCardId(id, creditCardId)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card subscription not found"));
	}

	public CreditCardSubscriptions create(long creditCardId, CreditCardSubscriptionRequest request) {

		var creditCard = findCreditCard(creditCardId);

		validateRequest(request);

		var subscription = mapper.toEntity(request);
		subscription.setCreditCard(creditCard);
		applyRelations(subscription, request);

		return subscriptionRepository.save(subscription);
	}

	public CreditCardSubscriptions update(long creditCardId, long id, CreditCardSubscriptionRequest request) {

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

		return subscriptionRepository.save(subscription);
	}

	public CreditCardSubscriptions activate(long creditCardId, long id) {

		var subscription = getById(creditCardId, id);
		subscription.setActive(true);

		return subscriptionRepository.save(subscription);
	}

	public CreditCardSubscriptions inactivate(long creditCardId, long id) {

		var subscription = getById(creditCardId, id);
		subscription.setActive(false);

		return subscriptionRepository.save(subscription);
	}

	private void applyRelations(
			CreditCardSubscriptions subscription, CreditCardSubscriptionRequest request) {

		var category = findCategory(request.getCategoryId());
		var subcategory = findSubcategory(request.getSubcategoryId(), category);
		var people = findPeople(request.getPersonIds());
		var tags = findTags(request.getTagIds());

		subscription.setCategory(category);
		subscription.setSubcategory(subcategory);
		subscription.setPeople(people);
		subscription.setTags(tags);
	}

	private void validateRequest(CreditCardSubscriptionRequest request) {

		validateRequiredFields(request);
		validateDates(request);
	}

	private void validateRequiredFields(CreditCardSubscriptionRequest request) {

		if (request == null
				|| request.getCategoryId() == null
				|| request.getSubcategoryId() == null
				|| request.getDescription() == null || request.getDescription().isBlank()
				|| request.getAmount() == null
				|| request.getStartDate() == null
				|| request.getEndDate() == null) {

			throw new BusinessRuleException(
					"Category, subcategory, description, amount, start date and end date are required");
		}
	}

	private void validateDates(CreditCardSubscriptionRequest request) {

		if (!request.getEndDate().isAfter(request.getStartDate())) {
			throw new BusinessRuleException("End date must be after start date");
		}
	}

	private CreditCard findCreditCard(Long id) {
		return creditCardRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
	}

	private Category findCategory(Long id) {

		var category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		if (category.getType() != CategoryType.EXPENSE) {
			throw new BusinessRuleException(
					"Category type must be Expense for credit card subscriptions");
		}

		return category;
	}

	private Subcategory findSubcategory(Long id, Category category) {

		var subcategory = subcategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));

		if (!subcategory.getCategory().getId().equals(category.getId())) {
			throw new BusinessRuleException(
					"Subcategory does not belong to the informed category");
		}

		return subcategory;
	}

	private List<Person> findPeople(List<Long> personIds) {

		if (personIds == null || personIds.isEmpty()) {
			return List.of();
		}

		if (personIds.contains(null)) {
			throw new BusinessRuleException("Person is required");
		}

		var ids = personIds.stream().distinct().toList();
		var people = personRepository.findAllById(ids);

		if (people.size() != ids.size()) {
			throw new BusinessRuleException("One or more people were not found");
		}

		return people;
	}

	private List<Tag> findTags(List<Long> tagIds) {

		if (tagIds == null || tagIds.isEmpty()) {
			return List.of();
		}

		if (tagIds.contains(null)) {
			throw new BusinessRuleException("Tag is required");
		}

		var ids = tagIds.stream().distinct().toList();
		var tags = tagRepository.findAllById(ids);

		if (tags.size() != ids.size()) {
			throw new BusinessRuleException("One or more tags were not found");
		}

		return tags;
	}
}
