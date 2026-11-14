package com.leandro.monthly_budget.model.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.leandro.monthly_budget.model.dtos.requests.FixedExpenseRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.FixedExpense;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.FixedExpenseMapper;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.FixedExpenseRepository;
import com.leandro.monthly_budget.model.repositories.PersonRepository;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;
import com.leandro.monthly_budget.model.repositories.TagRepository;
import com.leandro.monthly_budget.model.services.interfaces.FixedExpenseService;

@Service
public class FixedExpenseServiceImpl implements FixedExpenseService {

	private FixedExpenseRepository fixedExpenseRepository;
	private CategoryRepository categoryRepository;
	private SubcategoryRepository subcategoryRepository;
	private PersonRepository personRepository;
	private TagRepository tagRepository;
	private FixedExpenseMapper mapper;

	public FixedExpenseServiceImpl(
			FixedExpenseRepository fixedExpenseRepository,
			CategoryRepository categoryRepository,
			SubcategoryRepository subcategoryRepository,
			PersonRepository personRepository,
			TagRepository tagRepository,
			FixedExpenseMapper mapper) {
		super();
		this.fixedExpenseRepository = fixedExpenseRepository;
		this.categoryRepository = categoryRepository;
		this.subcategoryRepository = subcategoryRepository;
		this.personRepository = personRepository;
		this.tagRepository = tagRepository;
		this.mapper = mapper;
	}

	public Page<FixedExpense> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> fixedExpenseRepository.findAll(pageable);
			case ACTIVE -> fixedExpenseRepository.findAllByStatus(true, pageable);
			case INACTIVE -> fixedExpenseRepository.findAllByStatus(false, pageable);
		};
	}

	public FixedExpense getById(long id) {
		return fixedExpenseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fixed expense not found"));
	}

	public FixedExpense create(FixedExpenseRequest request) {

		validateRequest(request);

		var fixedExpense = mapper.toEntity(request);
		applyRelations(fixedExpense, request);

		return fixedExpenseRepository.save(fixedExpense);
	}

	public FixedExpense update(long id, FixedExpenseRequest request) {

		var fixedExpense = getById(id);

		validateRequest(request);

		fixedExpense.setStore(request.getStore());
		fixedExpense.setDescription(request.getDescription());
		fixedExpense.setAmount(request.getAmount());
		fixedExpense.setStartDate(request.getStartDate());
		fixedExpense.setEndDate(request.getEndDate());

		if (request.getIgnore() != null) {
			fixedExpense.setIgnore(request.getIgnore());
		}

		applyRelations(fixedExpense, request);

		return fixedExpenseRepository.save(fixedExpense);
	}

	public FixedExpense activate(long id) {

		var fixedExpense = getById(id);
		fixedExpense.setActive(true);

		return fixedExpenseRepository.save(fixedExpense);
	}

	public FixedExpense inactivate(long id) {

		var fixedExpense = getById(id);
		fixedExpense.setActive(false);

		return fixedExpenseRepository.save(fixedExpense);
	}

	private void applyRelations(FixedExpense fixedExpense, FixedExpenseRequest request) {

		var category = findCategory(request.getCategoryId());
		var subcategory = findSubcategory(request.getSubcategoryId(), category);
		var people = findPeople(request.getPersonIds());
		var tags = findTags(request.getTagIds());

		fixedExpense.setCategory(category);
		fixedExpense.setSubcategory(subcategory);
		fixedExpense.setPeople(people);
		fixedExpense.setTags(tags);
	}

	private void validateRequest(FixedExpenseRequest request) {

		validateRequiredFields(request);
		validateDates(request);
	}

	private void validateRequiredFields(FixedExpenseRequest request) {

		if (request == null
				|| request.getCategoryId() == null
				|| request.getSubcategoryId() == null
				|| request.getDescription() == null || request.getDescription().isBlank()
				|| request.getAmount() == null
				|| request.getStartDate() == null
				|| request.getEndDate() == null) {

			throw new BusinessRuleException(
					"Category, subcategory, description, amount, start date and end date are required");
		}
	}

	private void validateDates(FixedExpenseRequest request) {

		if (!request.getEndDate().isAfter(request.getStartDate())) {
			throw new BusinessRuleException("End date must be after start date");
		}
	}

	private Category findCategory(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
	}

	private Subcategory findSubcategory(Long id, Category category) {

		var subcategory = subcategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));

		if (!subcategory.getCategory().getId().equals(category.getId())) {
			throw new BusinessRuleException(
					"Subcategory does not belong to the informed category");
		}

		return subcategory;
	}

	private List<Person> findPeople(List<Long> personIds) {

		if (personIds == null || personIds.isEmpty()) {
			return List.of();
		}

		if (personIds.contains(null)) {
			throw new BusinessRuleException("Person is required");
		}

		var ids = personIds.stream().distinct().toList();
		var people = personRepository.findAllById(ids);

		if (people.size() != ids.size()) {
			throw new BusinessRuleException("One or more people were not found");
		}

		return people;
	}

	private List<Tag> findTags(List<Long> tagIds) {

		if (tagIds == null || tagIds.isEmpty()) {
			return List.of();
		}

		if (tagIds.contains(null)) {
			throw new BusinessRuleException("Tag is required");
		}

		var ids = tagIds.stream().distinct().toList();
		var tags = tagRepository.findAllById(ids);

		if (tags.size() != ids.size()) {
			throw new BusinessRuleException("One or more tags were not found");
		}

		return tags;
	}
}
