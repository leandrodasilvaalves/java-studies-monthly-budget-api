package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.IncomeTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.IncomeTransaction;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.enums.IncomeStatus;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.IncomeTransactionMapper;
import com.leandro.monthly_budget.model.repositories.IncomeTransactionRepository;
import com.leandro.monthly_budget.model.services.interfaces.IncomeTransactionService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class IncomeTransactionServiceImpl implements IncomeTransactionService {

	private final IncomeTransactionRepository incomeTransactionRepository;
	private final IncomeTransactionMapper mapper;
	private final ReferenceDataValidator validator;

	public IncomeTransactionServiceImpl(
			IncomeTransactionRepository incomeTransactionRepository,
			IncomeTransactionMapper mapper,
			ReferenceDataValidator validator) {
		this.incomeTransactionRepository = incomeTransactionRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	public Page<IncomeTransaction> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> incomeTransactionRepository.findAll(pageable);
			case ACTIVE -> incomeTransactionRepository.findAllByStatus(true, pageable);
			case INACTIVE -> incomeTransactionRepository.findAllByStatus(false, pageable);
		};
	}

	public IncomeTransaction getById(long id) {
		return incomeTransactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Income transaction not found"));
	}

	public IncomeTransaction create(IncomeTransactionRequest request) {

		validateRequest(request);

		var incomeTransaction = mapper.toEntity(request);
		applyRelations(incomeTransaction, request);

		return incomeTransactionRepository.save(incomeTransaction);
	}

	public IncomeTransaction update(long id, IncomeTransactionRequest request) {

		var incomeTransaction = getById(id);

		validateRequest(request);

		incomeTransaction.setPayer(request.getPayer());
		incomeTransaction.setDescription(request.getDescription());
		incomeTransaction.setAmount(request.getAmount());
		incomeTransaction.setStatus(request.getStatus());
		incomeTransaction.setReceivedDate(request.getReceivedDate());
		incomeTransaction.setIgnore(Boolean.TRUE.equals(request.getIgnore()));
		incomeTransaction.setNotes(request.getNotes());
		incomeTransaction.setRegisteredDate(request.getRegisteredDate());

		applyRelations(incomeTransaction, request);

		return incomeTransactionRepository.save(incomeTransaction);
	}

	public IncomeTransaction activate(long id) {

		var incomeTransaction = getById(id);
		incomeTransaction.setActive(true);

		return incomeTransactionRepository.save(incomeTransaction);
	}

	public IncomeTransaction inactivate(long id) {

		var incomeTransaction = getById(id);
		incomeTransaction.setActive(false);

		return incomeTransactionRepository.save(incomeTransaction);
	}

	private void validateRequest(IncomeTransactionRequest request) {

		validator.requireRequest(request);

		if (request.getStatus() == IncomeStatus.RECEIVED) {
			if (request.getReceivedDate() == null || request.getAccountId() == null) {
				throw new BusinessRuleException("RECEIVED incomes require receivedDate and account");
			}
		}

		if (request.getStatus() == IncomeStatus.PENDING) {
			if (request.getReceivedDate() != null || request.getAccountId() != null) {
				throw new BusinessRuleException("PENDING incomes must not have receivedDate or account");
			}
		}
	}

	private void applyRelations(IncomeTransaction incomeTransaction, IncomeTransactionRequest request) {

		var category = validator.requireCategory(
				request.getCategoryId(), CategoryType.INCOME, "income transactions");

		incomeTransaction.setCategory(category);
		incomeTransaction.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		incomeTransaction.setTags(validator.requireTags(request.getTagIds()));
		incomeTransaction.setAccount(
				request.getAccountId() == null ? null : validator.requireAccount(request.getAccountId()));
	}
}
