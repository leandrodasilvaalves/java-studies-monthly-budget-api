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

import com.leandro.monthly_budget.model.dtos.requests.CategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CategoryMapper;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

	@Mock private CategoryRepository repository;
	@Mock private CategoryMapper mapper;
	@InjectMocks private CategoryServiceImpl service;

	@Test
	void getAllListsEverythingWhenNoTypeIsInformed() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.<Category>of()));

		assertThat(service.getAll(1, 20, null, StatusRequest.ALL)).isEmpty();
		verify(repository).findAll(pageable);
	}

	@Test
	void getAllFiltersByTypeAndStatus() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findByTypeAndStatus(CategoryType.EXPENSE, true, pageable))
				.thenReturn(new PageImpl<>(List.<Category>of()));

		service.getAll(1, 20, CategoryType.EXPENSE, StatusRequest.ACTIVE);

		verify(repository).findByTypeAndStatus(CategoryType.EXPENSE, true, pageable);
	}

	@Test
	void getByIdThrowsWhenMissing() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Category not found");
	}

	@Test
	void createRejectsNameDuplicatedInTheSameType() {
		var request = new CategoryRequest("Food", CategoryType.EXPENSE);
		when(repository.existsByNameAndType("Food", CategoryType.EXPENSE)).thenReturn(true);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Already exists a category with this name and type");
	}

	@Test
	void updateChangesNameAndType() {
		var category = new Category("Old", CategoryType.INCOME);
		when(repository.findById(1L)).thenReturn(Optional.of(category));
		when(repository.save(category)).thenReturn(category);

		var updated = service.update(1L, new CategoryRequest("New", CategoryType.EXPENSE));

		assertThat(updated.getName()).isEqualTo("New");
		assertThat(updated.getType()).isEqualTo(CategoryType.EXPENSE);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var category = new Category("Food", CategoryType.EXPENSE);
		when(repository.findById(1L)).thenReturn(Optional.of(category));
		when(repository.save(category)).thenReturn(category);

		service.inactivate(1L);
		assertThat(category.isActive()).isFalse();

		service.activate(1L);
		assertThat(category.isActive()).isTrue();
	}
}
