package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.FixedExpenseRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.FixedExpense;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.FixedExpenseMapper;
import com.leandro.monthly_budget.model.repositories.FixedExpenseRepository;
import com.leandro.monthly_budget.model.services.interfaces.FixedExpenseService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class FixedExpenseServiceImpl implements FixedExpenseService {

	private final FixedExpenseRepository fixedExpenseRepository;
	private final FixedExpenseMapper mapper;
	private final ReferenceDataValidator validator;

	public FixedExpenseServiceImpl(
			FixedExpenseRepository fixedExpenseRepository,
			FixedExpenseMapper mapper,
			ReferenceDataValidator validator) {
		this.fixedExpenseRepository = fixedExpenseRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	public Page<FixedExpense> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> fixedExpenseRepository.findAll(pageable);
			case ACTIVE -> fixedExpenseRepository.findAllByStatus(true, pageable);
			case INACTIVE -> fixedExpenseRepository.findAllByStatus(false, pageable);
		};
	}

	public FixedExpense getById(long id) {
		return fixedExpenseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fixed expense not found"));
	}

	public FixedExpense create(FixedExpenseRequest request) {

		validateRequest(request);

		var fixedExpense = mapper.toEntity(request);
		applyRelations(fixedExpense, request);

		return fixedExpenseRepository.save(fixedExpense);
	}

	public FixedExpense update(long id, FixedExpenseRequest request) {

		var fixedExpense = getById(id);

		validateRequest(request);

		fixedExpense.setStore(request.getStore());
		fixedExpense.setDescription(request.getDescription());
		fixedExpense.setAmount(request.getAmount());
		fixedExpense.setStartDate(request.getStartDate());
		fixedExpense.setEndDate(request.getEndDate());

		if (request.getIgnore() != null) {
			fixedExpense.setIgnore(request.getIgnore());
		}

		applyRelations(fixedExpense, request);

		return fixedExpenseRepository.save(fixedExpense);
	}

	public FixedExpense activate(long id) {

		var fixedExpense = getById(id);
		fixedExpense.setActive(true);

		return fixedExpenseRepository.save(fixedExpense);
	}

	public FixedExpense inactivate(long id) {

		var fixedExpense = getById(id);
		fixedExpense.setActive(false);

		return fixedExpenseRepository.save(fixedExpense);
	}

	private void validateRequest(FixedExpenseRequest request) {

		validator.requireRequest(request);
		validator.requireDateOrder(request.getStartDate(), request.getEndDate());
	}

	private void applyRelations(FixedExpense fixedExpense, FixedExpenseRequest request) {

		var category = validator.requireCategory(
				request.getCategoryId(), CategoryType.EXPENSE, "fixed expenses");

		fixedExpense.setCategory(category);
		fixedExpense.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		fixedExpense.setPeople(validator.requirePeople(request.getPersonIds()));
		fixedExpense.setTags(validator.requireTags(request.getTagIds()));
	}
}
