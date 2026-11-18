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

import com.leandro.monthly_budget.model.dtos.requests.TagRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.TagMapper;
import com.leandro.monthly_budget.model.repositories.TagRepository;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

	@Mock private TagRepository repository;
	@Mock private TagMapper mapper;
	@InjectMocks private TagServiceImpl service;

	@Test
	void getAllListsEverythingWhenStatusIsAll() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.<Tag>of()));

		assertThat(service.getAll(1, 20, StatusRequest.ALL)).isEmpty();
		verify(repository).findAll(pageable);
	}

	@Test
	void getAllFiltersByStatus() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByStatus(false, pageable)).thenReturn(new PageImpl<>(List.<Tag>of()));

		service.getAll(1, 20, StatusRequest.INACTIVE);

		verify(repository).findAllByStatus(false, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Tag not found");
	}

	@Test
	void createRejectsDuplicatedName() {
		var request = new TagRequest("amortização");
		when(repository.existsByName("amortização")).thenReturn(true);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Already exists a tag with this name");
	}

	@Test
	void updateChangesOnlyTheName() {
		var tag = new Tag("Old");
		when(repository.findById(1L)).thenReturn(Optional.of(tag));
		when(repository.save(tag)).thenReturn(tag);

		var updated = service.update(1L, new TagRequest("New"));

		assertThat(updated.getName()).isEqualTo("New");
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var tag = new Tag("amortização");
		when(repository.findById(1L)).thenReturn(Optional.of(tag));
		when(repository.save(tag)).thenReturn(tag);

		service.inactivate(1L);
		assertThat(tag.isActive()).isFalse();

		service.activate(1L);
		assertThat(tag.isActive()).isTrue();
	}
}
