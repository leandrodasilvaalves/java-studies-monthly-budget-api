package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.CreditCardBill;
import com.leandro.monthly_budget.model.entities.CreditCardTransaction;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.enums.CreditCardTransactionStatus;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CreditCardTransactionMapper;
import com.leandro.monthly_budget.model.repositories.CreditCardTransactionRepository;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardBillService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@ExtendWith(MockitoExtension.class)
class CreditCardTransactionServiceImplTest {

	@Mock private CreditCardTransactionRepository repository;
	@Mock private CreditCardTransactionMapper mapper;
	@Mock private ReferenceDataValidator validator;
	@Mock private CreditCardBillService billService;
	@InjectMocks private CreditCardTransactionServiceImpl service;

	@Test
	void getAllValidatesCreditCardAndListsTransactions() {
		var pageable = PageRequest.of(0, 20);
		var transaction = new CreditCardTransaction();
		when(repository.findAllByCreditCardId(7L, pageable))
				.thenReturn(new PageImpl<>(List.of(transaction)));

		var result = service.getAll(7L, 1, 20, StatusRequest.ALL);

		assertThat(result.getContent()).containsExactly(transaction);
		verify(validator).requireCreditCard(7L);
		verify(repository).findAllByCreditCardId(7L, pageable);
	}

	@Test
	void getAllFiltersByStatus() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByCreditCardIdAndStatus(7L, true, pageable))
				.thenReturn(new PageImpl<>(List.<CreditCardTransaction>of()));

		service.getAll(7L, 1, 20, StatusRequest.ACTIVE);

		verify(repository).findAllByCreditCardIdAndStatus(7L, true, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findByIdAndCreditCardId(99L, 7L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(7L, 99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Credit card transaction not found");
	}

	@Test
	void createUsesBillForRegisteredDateAndGeneratesPurchaseIdWhenMissing() {
		var request = request(LocalDate.of(2026, 1, 15), 3, 1);
		var bill = new CreditCardBill();
		var transaction = new CreditCardTransaction();
		var category = new Category("Expense", CategoryType.EXPENSE);
		var subcategory = new Subcategory("Food", category);
		var person = new Person("Leandro");
		var tag = new Tag("Work");

		when(billService.requireBill(7L, YearMonth.of(2026, 1))).thenReturn(bill);
		when(mapper.toEntity(request)).thenReturn(transaction);
		when(validator.requireCategory(1L, CategoryType.EXPENSE, "credit card transactions"))
				.thenReturn(category);
		when(validator.requireSubcategory(2L, category)).thenReturn(subcategory);
		when(validator.requirePeople(List.of(3L))).thenReturn(List.of(person));
		when(validator.requireTags(List.of(4L))).thenReturn(List.of(tag));
		when(repository.save(transaction)).thenReturn(transaction);

		var created = service.create(7L, request);

		assertThat(created.getCreditCardBill()).isSameAs(bill);
		assertThat(created.getPurchaseId()).isNotNull();
		assertThat(created.getCategory()).isSameAs(category);
		assertThat(created.getSubcategory()).isSameAs(subcategory);
		assertThat(created.getPeople()).containsExactly(person);
		assertThat(created.getTags()).containsExactly(tag);
		verify(repository).save(transaction);
	}

	@Test
	void createKeepsTheInformedPurchaseId() {
		var request = request(LocalDate.of(2026, 2, 10), 1, 1);
		var purchaseId = java.util.UUID.randomUUID();
		request.setPurchaseId(purchaseId);

		var bill = new CreditCardBill();
		var transaction = new CreditCardTransaction();
		transaction.setPurchaseId(purchaseId);
		var category = new Category("Expense", CategoryType.EXPENSE);

		when(billService.requireBill(7L, YearMonth.of(2026, 2))).thenReturn(bill);
		when(mapper.toEntity(request)).thenReturn(transaction);
		when(validator.requireCategory(1L, CategoryType.EXPENSE, "credit card transactions"))
				.thenReturn(category);
		when(validator.requireSubcategory(2L, category))
				.thenReturn(new Subcategory("Food", category));
		when(validator.requirePeople(List.of(3L))).thenReturn(List.of());
		when(validator.requireTags(List.of(4L))).thenReturn(List.of());
		when(repository.save(transaction)).thenReturn(transaction);

		var created = service.create(7L, request);

		assertThat(created.getPurchaseId()).isEqualTo(purchaseId);
	}

	@Test
	void createRejectsInstallmentNumberGreaterThanTotalInstallments() {
		var request = request(LocalDate.of(2026, 1, 15), 3, 4);

		assertThatThrownBy(() -> service.create(7L, request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Installment number must be between 1 and total installments");

		verifyNoInteractions(repository, mapper, billService);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var transaction = new CreditCardTransaction();
		transaction.setActive(false);
		when(repository.findByIdAndCreditCardId(5L, 7L)).thenReturn(Optional.of(transaction));
		when(repository.save(transaction)).thenReturn(transaction);

		service.activate(7L, 5L);
		assertThat(transaction.isActive()).isTrue();

		service.inactivate(7L, 5L);
		assertThat(transaction.isActive()).isFalse();
		verify(repository, times(2)).save(transaction);
	}

	private static CreditCardTransactionRequest request(
			LocalDate registeredDate, int totalInstallments, int installmentNumber) {

		var request = new CreditCardTransactionRequest();
		request.setRegisteredDate(registeredDate);
		request.setCategoryId(1L);
		request.setSubcategoryId(2L);
		request.setStore("Store");
		request.setDescription("Purchase");
		request.setAmount(BigDecimal.TEN);
		request.setTotalInstallments(totalInstallments);
		request.setInstallmentNumber(installmentNumber);
		request.setStatus(CreditCardTransactionStatus.PENDING);
		request.setPersonIds(List.of(3L));
		request.setTagIds(List.of(4L));

		return request;
	}
}
