package com.leandro.monthly_budget.model.services.support;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.repositories.AccountRepository;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.CreditCardRepository;
import com.leandro.monthly_budget.model.repositories.PersonRepository;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;
import com.leandro.monthly_budget.model.repositories.TagRepository;

@ExtendWith(MockitoExtension.class)
class ReferenceDataValidatorTest {

	@Mock private CategoryRepository categoryRepository;
	@Mock private SubcategoryRepository subcategoryRepository;
	@Mock private PersonRepository personRepository;
	@Mock private TagRepository tagRepository;
	@Mock private CreditCardRepository creditCardRepository;
	@Mock private AccountRepository accountRepository;

	@InjectMocks private ReferenceDataValidator validator;

	@Test
	void rejectsCategoryWithWrongType() {
		when(categoryRepository.findById(1L))
				.thenReturn(Optional.of(new Category("Despesas", CategoryType.EXPENSE)));

		assertThatThrownBy(() -> validator.requireCategory(1L, CategoryType.INCOME, "fixed incomes"))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Category type must be Income for fixed incomes");
	}

	@Test
	void acceptsCategoryWithExpectedType() {
		when(categoryRepository.findById(1L))
				.thenReturn(Optional.of(new Category("Receitas", CategoryType.INCOME)));

		assertThatCode(() -> validator.requireCategory(1L, CategoryType.INCOME, "fixed incomes"))
				.doesNotThrowAnyException();
	}

	@Test
	void rejectsMissingCategory() {
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> validator.requireCategory(1L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void rejectsSubcategoryFromAnotherCategory() {
		var category = new Category("Receitas", CategoryType.INCOME);
		category.setId(1L);
		var other = new Category("Outras", CategoryType.INCOME);
		other.setId(2L);

		when(subcategoryRepository.findById(10L))
				.thenReturn(Optional.of(new Subcategory("Salário", other)));

		assertThatThrownBy(() -> validator.requireSubcategory(10L, category))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Subcategory does not belong to the informed category");
	}

	@Test
	void rejectsEqualDates() {
		var date = LocalDate.of(2026, 1, 1);

		assertThatThrownBy(() -> validator.requireDateOrder(date, date))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Start date must be before end date");
	}

	@Test
	void acceptsOrderedDates() {
		assertThatCode(() -> validator.requireDateOrder(
				LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)))
				.doesNotThrowAnyException();
	}

	@Test
	void rejectsNullTagId() {
		assertThatThrownBy(() -> validator.requireTags(Arrays.asList(1L, null)))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Tag is required");
	}

	@Test
	void rejectsTagThatDoesNotExist() {
		when(tagRepository.findAllById(any())).thenReturn(List.of(new Tag("amortização")));

		assertThatThrownBy(() -> validator.requireTags(List.of(1L, 2L)))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("One or more tags were not found");
	}

	@Test
	void rejectsNullRequest() {
		assertThatThrownBy(() -> validator.requireRequest(null))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Request is required");
	}
}
