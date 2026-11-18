package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.leandro.monthly_budget.model.dtos.requests.AccountCreateRequest;
import com.leandro.monthly_budget.model.dtos.requests.AccountUpdateRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.AccountMapper;
import com.leandro.monthly_budget.model.repositories.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

	@Mock private AccountRepository repository;
	@Mock private AccountMapper mapper;
	@InjectMocks private AccountServiceImpl service;

	@Test
	void getAllListsEverythingWhenStatusIsAll() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.<Account>of()));

		assertThat(service.getAll(1, 20, StatusRequest.ALL)).isEmpty();
		verify(repository).findAll(pageable);
	}

	@Test
	void getAllFiltersByStatus() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByStatus(false, pageable)).thenReturn(new PageImpl<>(List.<Account>of()));

		service.getAll(1, 20, StatusRequest.INACTIVE);

		verify(repository).findAllByStatus(false, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Account not found");
	}

	@Test
	void createRejectsDuplicatedName() {
		var request = new AccountCreateRequest("Nubank", BigDecimal.TEN);
		when(repository.existsByName("Nubank")).thenReturn(true);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Already exists an account with this name");
	}

	@Test
	void createSavesMappedEntity() {
		var request = new AccountCreateRequest("Nubank", BigDecimal.TEN);
		var account = new Account("Nubank", BigDecimal.TEN);
		when(mapper.toEntity(request)).thenReturn(account);
		when(repository.save(account)).thenReturn(account);

		assertThat(service.create(request)).isSameAs(account);
		verify(repository).save(account);
	}

	@Test
	void updateChangesOnlyTheName() {
		var account = new Account("Old", BigDecimal.TEN);
		when(repository.findById(1L)).thenReturn(Optional.of(account));
		when(repository.save(account)).thenReturn(account);

		var updated = service.update(1L, new AccountUpdateRequest("New"));

		assertThat(updated.getName()).isEqualTo("New");
		assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.TEN);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var account = new Account("Nubank", BigDecimal.TEN);
		account.setActive(false);
		when(repository.findById(1L)).thenReturn(Optional.of(account));
		when(repository.save(account)).thenReturn(account);

		service.activate(1L);
		assertThat(account.isActive()).isTrue();

		service.inactivate(1L);
		assertThat(account.isActive()).isFalse();
		verify(repository, times(2)).save(account);
	}
}
