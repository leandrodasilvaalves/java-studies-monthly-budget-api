package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CreditCardMapper;
import com.leandro.monthly_budget.model.repositories.CreditCardRepository;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceImplTest {

	@Mock private CreditCardRepository repository;
	@Mock private CreditCardMapper mapper;
	@InjectMocks private CreditCardServiceImpl service;

	@Test
	void getAllListsEverythingWhenStatusIsAll() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.<CreditCard>of()));

		assertThat(service.getAll(1, 20, StatusRequest.ALL)).isEmpty();
		verify(repository).findAll(pageable);
	}

	@Test
	void getAllFiltersByStatus() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByStatus(true, pageable)).thenReturn(new PageImpl<>(List.<CreditCard>of()));

		service.getAll(1, 20, StatusRequest.ACTIVE);

		verify(repository).findAllByStatus(true, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Credit card not found");
	}

	@Test
	void createRejectsDuplicatedName() {
		var request = new CreditCardRequest("Nubank", 10);
		when(repository.existsByName("Nubank")).thenReturn(true);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Already exists a credit card with this name");
	}

	@Test
	void updateChangesNameAndDueDay() {
		var creditCard = new CreditCard();
		creditCard.setName("Old");
		creditCard.setDueDay(5);
		when(repository.findById(1L)).thenReturn(Optional.of(creditCard));
		when(repository.save(creditCard)).thenReturn(creditCard);

		var updated = service.update(1L, new CreditCardRequest("New", 15));

		assertThat(updated.getName()).isEqualTo("New");
		assertThat(updated.getDueDay()).isEqualTo(15);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var creditCard = new CreditCard();
		when(repository.findById(1L)).thenReturn(Optional.of(creditCard));
		when(repository.save(creditCard)).thenReturn(creditCard);

		service.inactivate(1L);
		assertThat(creditCard.isActive()).isFalse();

		service.activate(1L);
		assertThat(creditCard.isActive()).isTrue();
	}
}
