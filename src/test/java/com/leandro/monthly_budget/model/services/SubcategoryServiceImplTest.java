package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
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

import com.leandro.monthly_budget.model.dtos.requests.SubcategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.SubcategoryMapper;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@ExtendWith(MockitoExtension.class)
class SubcategoryServiceImplTest {

	@Mock private SubcategoryRepository repository;
	@Mock private SubcategoryMapper mapper;
	@Mock private ReferenceDataValidator validator;
	@InjectMocks private SubcategoryServiceImpl service;

	@Test
	void getAllListsEverythingFromTheCategory() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByCategory(1L, pageable))
				.thenReturn(new PageImpl<>(List.<Subcategory>of()));

		assertThat(service.getAll(1L, 1, 20, StatusRequest.ALL)).isEmpty();
		verify(repository).findAllByCategory(1L, pageable);
	}

	@Test
	void getAllFiltersByStatusInsideTheCategory() {
		var pageable = PageRequest.of(0, 20);
		when(repository.findAllByCategoryAndStatus(1L, false, pageable))
				.thenReturn(new PageImpl<>(List.<Subcategory>of()));

		service.getAll(1L, 1, 20, StatusRequest.INACTIVE);

		verify(repository).findAllByCategoryAndStatus(1L, false, pageable);
	}

	@Test
	void getByIdThrowsWhenMissingInsideTheCategory() {
		when(repository.findByIdAndCategoryId(99L, 1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(1L, 99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Subcategory not found");
	}

	@Test
	void createRejectsMissingCategory() {
		var request = new SubcategoryRequest("Feira");
		when(validator.requireCategory(1L))
				.thenThrow(new ResourceNotFoundException("Category not found"));

		assertThatThrownBy(() -> service.create(1L, request))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Category not found");
		verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
	}

	@Test
	void createRejectsNameDuplicatedInTheSameCategory() {
		var request = new SubcategoryRequest("Feira");
		when(validator.requireCategory(1L)).thenReturn(new Category("Habitação", CategoryType.EXPENSE));
		when(repository.existsByNameAndCategory("Feira", 1L)).thenReturn(true);

		assertThatThrownBy(() -> service.create(1L, request))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Already exists a subcategory with this name for this category");
	}

	@Test
	void createLinksTheMappedSubcategoryToItsCategory() {
		var request = new SubcategoryRequest("Feira");
		var category = new Category("Habitação", CategoryType.EXPENSE);
		var subcategory = new Subcategory();
		subcategory.setName("Feira");
		when(validator.requireCategory(1L)).thenReturn(category);
		when(mapper.toEntity(request)).thenReturn(subcategory);
		when(repository.save(subcategory)).thenReturn(subcategory);

		var created = service.create(1L, request);

		assertThat(created).isSameAs(subcategory);
		assertThat(created.getCategory()).isSameAs(category);
	}

	@Test
	void updateValidatesTheParentCategoryBeforeChangingTheName() {
		var request = new SubcategoryRequest("Nova feira");
		var category = new Category("Habitação", CategoryType.EXPENSE);
		var subcategory = new Subcategory();
		subcategory.setName("Feira");
		when(validator.requireCategory(1L)).thenReturn(category);
		when(repository.findByIdAndCategoryId(2L, 1L)).thenReturn(Optional.of(subcategory));
		when(repository.save(subcategory)).thenReturn(subcategory);

		var updated = service.update(1L, 2L, request);

		assertThat(updated.getName()).isEqualTo("Nova feira");
		verify(validator).requireCategory(1L);
	}

	@Test
	void activateAndInactivateChangeTheFlag() {
		var subcategory = new Subcategory();
		subcategory.setName("Feira");
		when(repository.findByIdAndCategoryId(2L, 1L)).thenReturn(Optional.of(subcategory));
		when(repository.save(subcategory)).thenReturn(subcategory);

		service.inactivate(1L, 2L);
		assertThat(subcategory.isActive()).isFalse();

		service.activate(1L, 2L);
		assertThat(subcategory.isActive()).isTrue();
	}
}
