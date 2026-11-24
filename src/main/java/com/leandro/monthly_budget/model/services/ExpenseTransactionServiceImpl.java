package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.ExpenseTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.ExpenseTransaction;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.enums.ExpenseStatus;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.ExpenseTransactionMapper;
import com.leandro.monthly_budget.model.repositories.ExpenseTransactionRepository;
import com.leandro.monthly_budget.model.services.interfaces.ExpenseTransactionService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class ExpenseTransactionServiceImpl implements ExpenseTransactionService {

	private final ExpenseTransactionRepository expenseTransactionRepository;
	private final ExpenseTransactionMapper mapper;
	private final ReferenceDataValidator validator;

	public ExpenseTransactionServiceImpl(
			ExpenseTransactionRepository expenseTransactionRepository,
			ExpenseTransactionMapper mapper,
			ReferenceDataValidator validator) {
		this.expenseTransactionRepository = expenseTransactionRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	public Page<ExpenseTransaction> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> expenseTransactionRepository.findAll(pageable);
			case ACTIVE -> expenseTransactionRepository.findAllByStatus(true, pageable);
			case INACTIVE -> expenseTransactionRepository.findAllByStatus(false, pageable);
		};
	}

	public ExpenseTransaction getById(long id) {
		return expenseTransactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense transaction not found"));
	}

	public ExpenseTransaction create(ExpenseTransactionRequest request) {

		validateRequest(request);

		var expenseTransaction = mapper.toEntity(request);
		applyRelations(expenseTransaction, request);

		return expenseTransactionRepository.save(expenseTransaction);
	}

	public ExpenseTransaction update(long id, ExpenseTransactionRequest request) {

		var expenseTransaction = getById(id);

		validateRequest(request);

		expenseTransaction.setStore(request.getStore());
		expenseTransaction.setDescription(request.getDescription());
		expenseTransaction.setAmount(request.getAmount());
		expenseTransaction.setStatus(request.getStatus());
		expenseTransaction.setPaidDate(request.getPaidDate());
		expenseTransaction.setIgnore(Boolean.TRUE.equals(request.getIgnore()));
		expenseTransaction.setNotes(request.getNotes());
		expenseTransaction.setRegisteredDate(request.getRegisteredDate());

		applyRelations(expenseTransaction, request);

		return expenseTransactionRepository.save(expenseTransaction);
	}

	public ExpenseTransaction activate(long id) {

		var expenseTransaction = getById(id);
		expenseTransaction.setActive(true);

		return expenseTransactionRepository.save(expenseTransaction);
	}

	public ExpenseTransaction inactivate(long id) {

		var expenseTransaction = getById(id);
		expenseTransaction.setActive(false);

		return expenseTransactionRepository.save(expenseTransaction);
	}

	private void validateRequest(ExpenseTransactionRequest request) {

		validator.requireRequest(request);

		if (request.getStatus() == ExpenseStatus.PAID) {
			if (request.getPaidDate() == null || request.getAccountId() == null) {
				throw new BusinessRuleException("PAID expenses require paidDate and account");
			}
		}

		if (request.getStatus() == ExpenseStatus.PENDING) {
			if (request.getPaidDate() != null || request.getAccountId() != null) {
				throw new BusinessRuleException("PENDING expenses must not have paidDate or account");
			}
		}
	}

	private void applyRelations(ExpenseTransaction expenseTransaction, ExpenseTransactionRequest request) {

		var category = validator.requireCategory(
				request.getCategoryId(), CategoryType.EXPENSE, "expense transactions");

		expenseTransaction.setCategory(category);
		expenseTransaction.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		expenseTransaction.setPeople(validator.requirePeople(request.getPersonIds()));
		expenseTransaction.setTags(validator.requireTags(request.getTagIds()));
		expenseTransaction.setAccount(
				request.getAccountId() == null ? null : validator.requireAccount(request.getAccountId()));
	}
}
