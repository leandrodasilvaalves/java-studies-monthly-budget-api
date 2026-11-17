package com.leandro.monthly_budget.model.services.support;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.entities.Person;
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

/**
 * Buscas e validacoes de dados de referencia compartilhadas pelos services.
 *
 * <p>Substitui as copias de {@code findCategory}, {@code findSubcategory},
 * {@code findPeople}, {@code findTags} e validacoes de periodo que existiam
 * repetidas em cada implementacao.
 */
@Component
public class ReferenceDataValidator {

	private final CategoryRepository categoryRepository;
	private final SubcategoryRepository subcategoryRepository;
	private final PersonRepository personRepository;
	private final TagRepository tagRepository;
	private final CreditCardRepository creditCardRepository;
	private final AccountRepository accountRepository;

	public ReferenceDataValidator(
			CategoryRepository categoryRepository,
			SubcategoryRepository subcategoryRepository,
			PersonRepository personRepository,
			TagRepository tagRepository,
			CreditCardRepository creditCardRepository,
			AccountRepository accountRepository) {
		this.categoryRepository = categoryRepository;
		this.subcategoryRepository = subcategoryRepository;
		this.personRepository = personRepository;
		this.tagRepository = tagRepository;
		this.creditCardRepository = creditCardRepository;
		this.accountRepository = accountRepository;
	}

	public void requireRequest(Object request) {
		if (request == null) {
			throw new BusinessRuleException("Request is required");
		}
	}

	public void requireDateOrder(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
			throw new BusinessRuleException("Start date must be before end date");
		}
	}

	public Category requireCategory(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
	}

	public Category requireCategory(Long id, CategoryType expectedType, String context) {
		var category = requireCategory(id);

		if (category.getType() != expectedType) {
			throw new BusinessRuleException(
					"Category type must be " + label(expectedType) + " for " + context);
		}

		return category;
	}

	public Subcategory requireSubcategory(Long id, Category category) {
		var subcategory = subcategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));

		if (!subcategory.getCategory().getId().equals(category.getId())) {
			throw new BusinessRuleException(
					"Subcategory does not belong to the informed category");
		}

		return subcategory;
	}

	public Person requirePerson(Long id) {
		if (id == null) {
			return null;
		}

		return personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Person not found"));
	}

	public List<Person> requirePeople(List<Long> personIds) {
		if (personIds == null || personIds.isEmpty()) {
			return List.of();
		}

		if (personIds.stream().anyMatch(Objects::isNull)) {
			throw new BusinessRuleException("Person is required");
		}

		var ids = personIds.stream().distinct().toList();
		var people = personRepository.findAllById(ids);

		if (people.size() != ids.size()) {
			throw new BusinessRuleException("One or more people were not found");
		}

		return people;
	}

	public List<Tag> requireTags(List<Long> tagIds) {
		if (tagIds == null || tagIds.isEmpty()) {
			return List.of();
		}

		if (tagIds.stream().anyMatch(Objects::isNull)) {
			throw new BusinessRuleException("Tag is required");
		}

		var ids = tagIds.stream().distinct().toList();
		var tags = tagRepository.findAllById(ids);

		if (tags.size() != ids.size()) {
			throw new BusinessRuleException("One or more tags were not found");
		}

		return tags;
	}

	public CreditCard requireCreditCard(Long id) {
		return creditCardRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
	}

	public Account requireAccount(Long id) {
		return accountRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
	}

	private static String label(CategoryType type) {
		return type == CategoryType.INCOME ? "Income" : "Expense";
	}
}
