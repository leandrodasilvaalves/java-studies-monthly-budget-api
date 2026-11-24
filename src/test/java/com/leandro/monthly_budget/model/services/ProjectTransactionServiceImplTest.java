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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.leandro.monthly_budget.model.dtos.requests.ProjectTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Project;
import com.leandro.monthly_budget.model.entities.ProjectTransaction;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.enums.ProjectTransactionStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionType;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.ProjectTransactionMapper;
import com.leandro.monthly_budget.model.repositories.ProjectTransactionRepository;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@ExtendWith(MockitoExtension.class)
class ProjectTransactionServiceImplTest {

	@Mock private ProjectTransactionRepository repository;
	@Mock private ProjectTransactionMapper mapper;
	@Mock private ReferenceDataValidator validator;
	@InjectMocks private ProjectTransactionServiceImpl service;

	@Test
	void getAllValidatesProjectAndListsTransactions() {
		var pageable = PageRequest.of(0, 20);
		var project = new Project();
		var transaction = new ProjectTransaction();

		when(validator.requireProject(1L)).thenReturn(project);
		when(repository.findAllByProjectId(1L, pageable))
				.thenReturn(new PageImpl<>(List.of(transaction)));

		var result = service.getAll(1L, 1, 20, StatusRequest.ALL);

		assertThat(result).containsExactly(transaction);
		verify(validator).requireProject(1L);
		verify(repository).findAllByProjectId(1L, pageable);
	}

	@Test
	void getAllFiltersByStatus() {
		var pageable = PageRequest.of(0, 20);

		when(validator.requireProject(1L)).thenReturn(new Project());
		when(repository.findAllByProjectIdAndStatus(1L, true, pageable))
				.thenReturn(Page.empty(pageable));

		service.getAll(1L, 1, 20, StatusRequest.ACTIVE);

		verify(repository).findAllByProjectIdAndStatus(1L, true, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findByIdAndProjectId(99L, 1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(1L, 99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Project transaction not found");
	}

	@Test
	void createRejectsDoneWithoutMovementDate() {
		var request = validRequest();
		request.setMovementDate(null);

		assertThatThrownBy(() -> service.create(1L, request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("DONE project transactions require movementDate");
	}

	@Test
	void createRejectsPendingWithoutDueDate() {
		var request = validRequest();
		request.setStatus(ProjectTransactionStatus.PENDING);
		request.setDueDate(null);
		request.setMovementDate(null);

		assertThatThrownBy(() -> service.create(1L, request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("PENDING project transactions require dueDate and must not have movementDate");
	}

	@Test
	void createRejectsYieldWithAccount() {
		var request = validRequest();
		request.setProjectTransactionType(ProjectTransactionType.YIELD);

		assertThatThrownBy(() -> service.create(1L, request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("YIELD project transactions must not have an account");
	}

	@Test
	void createRejectsDepositWithoutAccount() {
		var request = validRequest();
		request.setAccountId(null);

		assertThatThrownBy(() -> service.create(1L, request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("DEPOSIT and WITHDRAWAL project transactions require an account");
	}

	@Test
	void createSavesMappedEntityWithRelations() {
		var request = validRequest();
		var entity = new ProjectTransaction();
		var project = new Project();
		var category = new Category("Investments", CategoryType.EXPENSE);
		var subcategory = new Subcategory("Contributions", category);
		var people = List.of(new Person("Ana"));
		var tags = List.of(new Tag("monthly"));
		var account = new Account("Broker", BigDecimal.ZERO);

		when(mapper.toEntity(request)).thenReturn(entity);
		when(validator.requireProject(7L)).thenReturn(project);
		when(validator.requireCategory(2L)).thenReturn(category);
		when(validator.requireSubcategory(3L, category)).thenReturn(subcategory);
		when(validator.requirePeople(List.of(10L))).thenReturn(people);
		when(validator.requireTags(List.of(11L))).thenReturn(tags);
		when(validator.requireAccount(4L)).thenReturn(account);
		when(repository.save(entity)).thenReturn(entity);

		var created = service.create(7L, request);

		assertThat(created).isSameAs(entity);
		assertThat(created.getProject()).isSameAs(project);
		assertThat(created.getCategory()).isSameAs(category);
		assertThat(created.getSubcategory()).isSameAs(subcategory);
		assertThat(created.getPeople()).isSameAs(people);
		assertThat(created.getTags()).isSameAs(tags);
		assertThat(created.getAccount()).isSameAs(account);
		verify(repository).save(entity);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var entity = new ProjectTransaction();
		entity.setActive(false);

		when(repository.findByIdAndProjectId(1L, 5L)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(entity);

		service.activate(5L, 1L);
		assertThat(entity.isActive()).isTrue();

		service.inactivate(5L, 1L);
		assertThat(entity.isActive()).isFalse();

		verify(repository, times(2)).save(entity);
	}

	private static ProjectTransactionRequest validRequest() {
		var request = new ProjectTransactionRequest();
		request.setRegisteredDate(LocalDate.of(2026, 1, 1));
		request.setCategoryId(2L);
		request.setSubcategoryId(3L);
		request.setDescription("Monthly deposit");
		request.setAmount(BigDecimal.valueOf(500));
		request.setProjectTransactionType(ProjectTransactionType.DEPOSIT);
		request.setStatus(ProjectTransactionStatus.DONE);
		request.setMovementDate(LocalDate.of(2026, 1, 2));
		request.setAccountId(4L);
		request.setPersonIds(List.of(10L));
		request.setTagIds(List.of(11L));
		request.setIgnore(false);
		request.setNotes("note");
		return request;
	}
}
