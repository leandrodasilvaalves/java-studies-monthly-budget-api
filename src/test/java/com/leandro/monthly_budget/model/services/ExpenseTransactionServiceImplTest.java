package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.leandro.monthly_budget.model.dtos.requests.ExpenseTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.ExpenseTransaction;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.enums.ExpenseStatus;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.ExpenseTransactionMapper;
import com.leandro.monthly_budget.model.repositories.ExpenseTransactionRepository;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@ExtendWith(MockitoExtension.class)
class ExpenseTransactionServiceImplTest {

	@Mock private ExpenseTransactionRepository repository;
	@Mock private ExpenseTransactionMapper mapper;
	@Mock private ReferenceDataValidator validator;
	@InjectMocks private ExpenseTransactionServiceImpl service;

	@Test
	void getAllListsEverythingWhenStatusIsAll() {
		var pageable = PageRequest.of(0, 20);
		var transaction = new ExpenseTransaction();
		when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(transaction)));

		var result = service.getAll(1, 20, StatusRequest.ALL);

		assertThat(result.getContent()).containsExactly(transaction);
		verify(repository).findAll(pageable);
	}

	@Test
	void getAllFiltersByStatus() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByStatus(false, pageable)).thenReturn(new PageImpl<>(List.of()));

		service.getAll(1, 20, StatusRequest.INACTIVE);

		verify(repository).findAllByStatus(false, pageable);
	}

	@Test
	void getAllFiltersActiveTransactions() {
		var pageable = PageRequest.of(1, 10);
		when(repository.findAllByStatus(true, pageable)).thenReturn(new PageImpl<>(List.of()));

		service.getAll(2, 10, StatusRequest.ACTIVE);

		verify(repository).findAllByStatus(true, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Expense transaction not found");
	}

	@Test
	void getByIdReturnsTheTransaction() {
		var transaction = new ExpenseTransaction();
		when(repository.findById(1L)).thenReturn(Optional.of(transaction));

		assertThat(service.getById(1L)).isSameAs(transaction);
	}

	@Test
	void createRejectsPaidWithoutPaymentData() {
		var request = new ExpenseTransactionRequest();
		request.setStatus(ExpenseStatus.PAID);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("PAID expenses require paidDate and account");
	}

	@Test
	void createRejectsPendingWithPaymentData() {
		var request = new ExpenseTransactionRequest();
		request.setStatus(ExpenseStatus.PENDING);
		request.setPaidDate(LocalDate.of(2026, 1, 10));

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("PENDING expenses must not have paidDate or account");
	}

	@Test
	void createSavesMappedEntity() {
		var request = new ExpenseTransactionRequest();
		request.setRegisteredDate(LocalDate.of(2026, 1, 10));
		request.setCategoryId(1L);
		request.setSubcategoryId(2L);
		request.setDescription("Groceries");
		request.setAmount(new BigDecimal("100.00"));
		request.setStatus(ExpenseStatus.PENDING);
		request.setPersonIds(List.of(3L));
		request.setTagIds(List.of(4L));

		var category = new Category("Food", CategoryType.EXPENSE);
		var subcategory = new Subcategory("Market", category);
		var person = new Person("Ana");
		var tag = new Tag("home");
		var transaction = new ExpenseTransaction();

		when(mapper.toEntity(request)).thenReturn(transaction);
		when(validator.requireCategory(1L, CategoryType.EXPENSE, "expense transactions"))
				.thenReturn(category);
		when(validator.requireSubcategory(2L, category)).thenReturn(subcategory);
		when(validator.requirePeople(List.of(3L))).thenReturn(List.of(person));
		when(validator.requireTags(List.of(4L))).thenReturn(List.of(tag));
		when(repository.save(transaction)).thenReturn(transaction);

		var created = service.create(request);

		assertThat(created).isSameAs(transaction);
		assertThat(transaction.getCategory()).isSameAs(category);
		assertThat(transaction.getSubcategory()).isSameAs(subcategory);
		assertThat(transaction.getPeople()).containsExactly(person);
		assertThat(transaction.getTags()).containsExactly(tag);
		assertThat(transaction.getAccount()).isNull();
		verify(repository).save(transaction);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var transaction = new ExpenseTransaction();
		transaction.setActive(false);
		when(repository.findById(1L)).thenReturn(Optional.of(transaction));
		when(repository.save(transaction)).thenReturn(transaction);

		service.activate(1L);
		assertThat(transaction.isActive()).isTrue();

		service.inactivate(1L);
		assertThat(transaction.isActive()).isFalse();

		verify(repository, times(2)).save(transaction);
	}
}
