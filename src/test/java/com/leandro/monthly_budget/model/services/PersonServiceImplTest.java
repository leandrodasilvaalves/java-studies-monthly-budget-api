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

import com.leandro.monthly_budget.model.dtos.requests.PersonRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.PersonMapper;
import com.leandro.monthly_budget.model.repositories.PersonRepository;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

	@Mock private PersonRepository repository;
	@Mock private PersonMapper mapper;
	@InjectMocks private PersonServiceImpl service;

	@Test
	void getAllListsEverythingWhenStatusIsAll() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.<Person>of()));

		assertThat(service.getAll(1, 20, StatusRequest.ALL)).isEmpty();
		verify(repository).findAll(pageable);
	}

	@Test
	void getAllFiltersByStatus() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByStatus(true, pageable)).thenReturn(new PageImpl<>(List.<Person>of()));

		service.getAll(1, 20, StatusRequest.ACTIVE);

		verify(repository).findAllByStatus(true, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Person not found");
	}

	@Test
	void createRejectsDuplicatedName() {
		var request = new PersonRequest("Leandro");
		when(repository.existsByName("Leandro")).thenReturn(true);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Already exists a person with this name");
	}

	@Test
	void updateChangesOnlyTheName() {
		var person = new Person("Old");
		when(repository.findById(1L)).thenReturn(Optional.of(person));
		when(repository.save(person)).thenReturn(person);

		var updated = service.update(1L, new PersonRequest("New"));

		assertThat(updated.getName()).isEqualTo("New");
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var person = new Person("Leandro");
		when(repository.findById(1L)).thenReturn(Optional.of(person));
		when(repository.save(person)).thenReturn(person);

		service.inactivate(1L);
		assertThat(person.isActive()).isFalse();

		service.activate(1L);
		assertThat(person.isActive()).isTrue();
	}
}
