package com.leandro.monthly_budget.model.services;

import java.time.YearMonth;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCardTransaction;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CreditCardTransactionMapper;
import com.leandro.monthly_budget.model.repositories.CreditCardTransactionRepository;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardBillService;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardTransactionService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class CreditCardTransactionServiceImpl implements CreditCardTransactionService {

	private final CreditCardTransactionRepository transactionRepository;
	private final CreditCardTransactionMapper mapper;
	private final ReferenceDataValidator validator;
	private final CreditCardBillService billService;

	public CreditCardTransactionServiceImpl(
			CreditCardTransactionRepository transactionRepository,
			CreditCardTransactionMapper mapper,
			ReferenceDataValidator validator,
			CreditCardBillService billService) {
		this.transactionRepository = transactionRepository;
		this.mapper = mapper;
		this.validator = validator;
		this.billService = billService;
	}

	public Page<CreditCardTransaction> getAll(
			long creditCardId, int page, int size, StatusRequest status) {

		validator.requireCreditCard(creditCardId);

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> transactionRepository.findAllByCreditCardId(creditCardId, pageable);
			case ACTIVE -> transactionRepository.findAllByCreditCardIdAndStatus(creditCardId, true, pageable);
			case INACTIVE -> transactionRepository.findAllByCreditCardIdAndStatus(creditCardId, false, pageable);
		};
	}

	public CreditCardTransaction getById(long creditCardId, long id) {
		return transactionRepository.findByIdAndCreditCardId(id, creditCardId)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card transaction not found"));
	}

	public CreditCardTransaction create(long creditCardId, CreditCardTransactionRequest request) {

		validateRequest(request);

		var bill = billService.requireBill(
				creditCardId, YearMonth.from(request.getRegisteredDate()));

		var transaction = mapper.toEntity(request);
		transaction.setCreditCardBill(bill);
		applyRelations(transaction, request);

		if (transaction.getPurchaseId() == null) {
			transaction.setPurchaseId(UUID.randomUUID());
		}

		return transactionRepository.save(transaction);
	}

	public CreditCardTransaction update(
			long creditCardId, long id, CreditCardTransactionRequest request) {

		var transaction = getById(creditCardId, id);

		validateRequest(request);

		transaction.setRegisteredDate(request.getRegisteredDate());
		transaction.setStore(request.getStore());
		transaction.setDescription(request.getDescription());
		transaction.setAmount(request.getAmount());
		transaction.setTotalInstallments(request.getTotalInstallments());
		transaction.setInstallmentNumber(request.getInstallmentNumber());
		transaction.setStatus(request.getStatus());
		transaction.setPaidDate(request.getPaidDate());
		transaction.setNotes(request.getNotes());

		if (request.getPurchaseId() != null) {
			transaction.setPurchaseId(request.getPurchaseId());
		}

		if (request.getIgnore() != null) {
			transaction.setIgnore(request.getIgnore());
		}

		transaction.setCreditCardBill(billService.requireBill(
				creditCardId, YearMonth.from(request.getRegisteredDate())));

		applyRelations(transaction, request);

		return transactionRepository.save(transaction);
	}

	public CreditCardTransaction activate(long creditCardId, long id) {

		var transaction = getById(creditCardId, id);
		transaction.setActive(true);

		return transactionRepository.save(transaction);
	}

	public CreditCardTransaction inactivate(long creditCardId, long id) {

		var transaction = getById(creditCardId, id);
		transaction.setActive(false);

		return transactionRepository.save(transaction);
	}

	private void validateRequest(CreditCardTransactionRequest request) {

		validator.requireRequest(request);

		var totalInstallments = request.getTotalInstallments();
		var installmentNumber = request.getInstallmentNumber();

		if (totalInstallments == null || installmentNumber == null
				|| installmentNumber < 1 || installmentNumber > totalInstallments) {
			throw new BusinessRuleException(
					"Installment number must be between 1 and total installments");
		}
	}

	private void applyRelations(
			CreditCardTransaction transaction, CreditCardTransactionRequest request) {

		var category = validator.requireCategory(
				request.getCategoryId(), CategoryType.EXPENSE, "credit card transactions");

		transaction.setCategory(category);
		transaction.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		transaction.setPeople(validator.requirePeople(request.getPersonIds()));
		transaction.setTags(validator.requireTags(request.getTagIds()));
	}
}
