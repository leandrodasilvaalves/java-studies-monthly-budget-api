package com.leandro.monthly_budget.model.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.leandro.monthly_budget.model.dtos.requests.FixedIncomeRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.FixedIncome;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.FixedIncomeMapper;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.FixedIncomeRepository;
import com.leandro.monthly_budget.model.repositories.PersonRepository;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;
import com.leandro.monthly_budget.model.repositories.TagRepository;
import com.leandro.monthly_budget.model.services.interfaces.FixedIncomeService;

@Service
public class FixedIncomeServiceImpl implements FixedIncomeService {

	private FixedIncomeRepository fixedIncomeRepository;
	private CategoryRepository categoryRepository;
	private SubcategoryRepository subcategoryRepository;
	private PersonRepository personRepository;
	private TagRepository tagRepository;
	private FixedIncomeMapper mapper;

	public FixedIncomeServiceImpl(
			FixedIncomeRepository fixedIncomeRepository,
			CategoryRepository categoryRepository,
			SubcategoryRepository subcategoryRepository,
			PersonRepository personRepository,
			TagRepository tagRepository,
			FixedIncomeMapper mapper) {
		super();
		this.fixedIncomeRepository = fixedIncomeRepository;
		this.categoryRepository = categoryRepository;
		this.subcategoryRepository = subcategoryRepository;
		this.personRepository = personRepository;
		this.tagRepository = tagRepository;
		this.mapper = mapper;
	}

	public Page<FixedIncome> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> fixedIncomeRepository.findAll(pageable);
			case ACTIVE -> fixedIncomeRepository.findAllByStatus(true, pageable);
			case INACTIVE -> fixedIncomeRepository.findAllByStatus(false, pageable);
		};
	}

	public FixedIncome getById(long id) {
		return fixedIncomeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fixed income not found"));
	}

	public FixedIncome create(FixedIncomeRequest request) {

		validateRequest(request);

		var fixedIncome = mapper.toEntity(request);
		applyRelations(fixedIncome, request);

		return fixedIncomeRepository.save(fixedIncome);
	}

	public FixedIncome update(long id, FixedIncomeRequest request) {

		var fixedIncome = getById(id);

		validateRequest(request);

		fixedIncome.setPayer(request.getPayer());
		fixedIncome.setDescription(request.getDescription());
		fixedIncome.setAmount(request.getAmount());
		fixedIncome.setStartDate(request.getStartDate());
		fixedIncome.setEndDate(request.getEndDate());

		if (request.getIgnore() != null) {
			fixedIncome.setIgnore(request.getIgnore());
		}

		applyRelations(fixedIncome, request);

		return fixedIncomeRepository.save(fixedIncome);
	}

	public FixedIncome activate(long id) {

		var fixedIncome = getById(id);
		fixedIncome.setActive(true);

		return fixedIncomeRepository.save(fixedIncome);
	}

	public FixedIncome inactivate(long id) {

		var fixedIncome = getById(id);
		fixedIncome.setActive(false);

		return fixedIncomeRepository.save(fixedIncome);
	}

	private void applyRelations(FixedIncome fixedIncome, FixedIncomeRequest request) {

		var category = findCategory(request.getCategoryId());
		var subcategory = findSubcategory(request.getSubcategoryId(), category);
		var recipient = findRecipient(request.getRecipientId());
		var tags = findTags(request.getTagIds());

		fixedIncome.setCategory(category);
		fixedIncome.setSubcategory(subcategory);
		fixedIncome.setRecipient(recipient);
		fixedIncome.setTags(tags);
	}

	private void validateRequest(FixedIncomeRequest request) {

		validateRequiredFields(request);
		validateDates(request);
	}

	private void validateRequiredFields(FixedIncomeRequest request) {

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

	private void validateDates(FixedIncomeRequest request) {

		if (!request.getStartDate().isBefore(request.getEndDate())) {
			throw new BusinessRuleException("Start date must be before end date");
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

	private Person findRecipient(Long id) {

		if (id == null) {
			return null;
		}

		return personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Person not found"));
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
