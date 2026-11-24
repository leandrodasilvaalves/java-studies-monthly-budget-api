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

import com.leandro.monthly_budget.model.dtos.requests.IncomeTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.IncomeTransaction;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.enums.IncomeStatus;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.IncomeTransactionMapper;
import com.leandro.monthly_budget.model.repositories.IncomeTransactionRepository;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@ExtendWith(MockitoExtension.class)
class IncomeTransactionServiceImplTest {

	@Mock private IncomeTransactionRepository repository;
	@Mock private IncomeTransactionMapper mapper;
	@Mock private ReferenceDataValidator validator;
	@InjectMocks private IncomeTransactionServiceImpl service;

	@Test
	void getAllListsEverythingWhenStatusIsAll() {
		var pageable = PageRequest.of(0, 20);
		var transaction = new IncomeTransaction();
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
				.hasMessage("Income transaction not found");
	}

	@Test
	void getByIdReturnsTheTransaction() {
		var transaction = new IncomeTransaction();
		when(repository.findById(1L)).thenReturn(Optional.of(transaction));

		assertThat(service.getById(1L)).isSameAs(transaction);
	}

	@Test
	void createRejectsReceivedWithoutReceiptData() {
		var request = new IncomeTransactionRequest();
		request.setStatus(IncomeStatus.RECEIVED);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("RECEIVED incomes require receivedDate and account");
	}

	@Test
	void createRejectsPendingWithReceiptData() {
		var request = new IncomeTransactionRequest();
		request.setStatus(IncomeStatus.PENDING);
		request.setReceivedDate(LocalDate.of(2026, 1, 10));

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("PENDING incomes must not have receivedDate or account");
	}

	@Test
	void createSavesMappedEntity() {
		var request = new IncomeTransactionRequest();
		request.setRegisteredDate(LocalDate.of(2026, 1, 10));
		request.setCategoryId(1L);
		request.setSubcategoryId(2L);
		request.setDescription("Salary");
		request.setAmount(new BigDecimal("5000.00"));
		request.setStatus(IncomeStatus.PENDING);
		request.setTagIds(List.of(4L));

		var category = new Category("Salary", CategoryType.INCOME);
		var subcategory = new Subcategory("Monthly", category);
		var tag = new Tag("work");
		var transaction = new IncomeTransaction();

		when(mapper.toEntity(request)).thenReturn(transaction);
		when(validator.requireCategory(1L, CategoryType.INCOME, "income transactions"))
				.thenReturn(category);
		when(validator.requireSubcategory(2L, category)).thenReturn(subcategory);
		when(validator.requireTags(List.of(4L))).thenReturn(List.of(tag));
		when(repository.save(transaction)).thenReturn(transaction);

		var created = service.create(request);

		assertThat(created).isSameAs(transaction);
		assertThat(transaction.getCategory()).isSameAs(category);
		assertThat(transaction.getSubcategory()).isSameAs(subcategory);
		assertThat(transaction.getTags()).containsExactly(tag);
		assertThat(transaction.getAccount()).isNull();
		verify(repository).save(transaction);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var transaction = new IncomeTransaction();
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
