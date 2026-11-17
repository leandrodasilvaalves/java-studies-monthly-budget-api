package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.FixedIncomeRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.FixedIncome;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.FixedIncomeMapper;
import com.leandro.monthly_budget.model.repositories.FixedIncomeRepository;
import com.leandro.monthly_budget.model.services.interfaces.FixedIncomeService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class FixedIncomeServiceImpl implements FixedIncomeService {

	private final FixedIncomeRepository fixedIncomeRepository;
	private final FixedIncomeMapper mapper;
	private final ReferenceDataValidator validator;

	public FixedIncomeServiceImpl(
			FixedIncomeRepository fixedIncomeRepository,
			FixedIncomeMapper mapper,
			ReferenceDataValidator validator) {
		this.fixedIncomeRepository = fixedIncomeRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	public Page<FixedIncome> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> fixedIncomeRepository.findAll(pageable);
			case ACTIVE -> fixedIncomeRepository.findAllByStatus(true, pageable);
			case INACTIVE -> fixedIncomeRepository.findAllByStatus(false, pageable);
		};
	}

	public FixedIncome getById(long id) {
		return fixedIncomeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fixed income not found"));
	}

	public FixedIncome create(FixedIncomeRequest request) {

		validateRequest(request);

		var fixedIncome = mapper.toEntity(request);
		applyRelations(fixedIncome, request);

		return fixedIncomeRepository.save(fixedIncome);
	}

	public FixedIncome update(long id, FixedIncomeRequest request) {

		var fixedIncome = getById(id);

		validateRequest(request);

		fixedIncome.setPayer(request.getPayer());
		fixedIncome.setDescription(request.getDescription());
		fixedIncome.setAmount(request.getAmount());
		fixedIncome.setStartDate(request.getStartDate());
		fixedIncome.setEndDate(request.getEndDate());

		if (request.getIgnore() != null) {
			fixedIncome.setIgnore(request.getIgnore());
		}

		applyRelations(fixedIncome, request);

		return fixedIncomeRepository.save(fixedIncome);
	}

	public FixedIncome activate(long id) {

		var fixedIncome = getById(id);
		fixedIncome.setActive(true);

		return fixedIncomeRepository.save(fixedIncome);
	}

	public FixedIncome inactivate(long id) {

		var fixedIncome = getById(id);
		fixedIncome.setActive(false);

		return fixedIncomeRepository.save(fixedIncome);
	}

	private void validateRequest(FixedIncomeRequest request) {

		validator.requireRequest(request);
		validator.requireDateOrder(request.getStartDate(), request.getEndDate());
	}

	private void applyRelations(FixedIncome fixedIncome, FixedIncomeRequest request) {

		var category = validator.requireCategory(
				request.getCategoryId(), CategoryType.INCOME, "fixed incomes");

		fixedIncome.setCategory(category);
		fixedIncome.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		fixedIncome.setRecipient(validator.requirePerson(request.getRecipientId()));
		fixedIncome.setTags(validator.requireTags(request.getTagIds()));
	}
}
