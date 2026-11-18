package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.SubcategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.SubcategoryMapper;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;
import com.leandro.monthly_budget.model.services.interfaces.SubcategoryService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class SubcategoryServiceImpl implements SubcategoryService {

	private final SubcategoryRepository subcategoryRepository;
	private final SubcategoryMapper mapper;
	private final ReferenceDataValidator validator;

	public SubcategoryServiceImpl(
			SubcategoryRepository subcategoryRepository,
			SubcategoryMapper mapper,
			ReferenceDataValidator validator) {
		this.subcategoryRepository = subcategoryRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	public Page<Subcategory> getAll(long categoryId, int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> subcategoryRepository.findAllByCategory(categoryId, pageable);
			case ACTIVE -> subcategoryRepository.findAllByCategoryAndStatus(categoryId, true, pageable);
			case INACTIVE -> subcategoryRepository.findAllByCategoryAndStatus(categoryId, false, pageable);
		};
	}

	public Subcategory getById(long categoryId, long id) {
		return subcategoryRepository.findByIdAndCategoryId(id, categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
	}

	public Subcategory create(long categoryId, SubcategoryRequest request) {

		var category = validator.requireCategory(categoryId);

		if (subcategoryRepository.existsByNameAndCategory(request.getName(), categoryId)) {
			throw new DuplicateResourceException(
					"Already exists a subcategory with this name for this category");
		}

		var subcategory = mapper.toEntity(request);
		subcategory.setCategory(category);

		return subcategoryRepository.save(subcategory);
	}

	public Subcategory update(long categoryId, long id, SubcategoryRequest request) {

		validator.requireCategory(categoryId);

		var subcategory = getById(categoryId, id);
		subcategory.setName(request.getName());

		return subcategoryRepository.save(subcategory);
	}

	public Subcategory activate(long categoryId, long id) {

		var subcategory = getById(categoryId, id);
		subcategory.setActive(true);

		return subcategoryRepository.save(subcategory);
	}

	public Subcategory inactivate(long categoryId, long id) {

		var subcategory = getById(categoryId, id);
		subcategory.setActive(false);

		return subcategoryRepository.save(subcategory);
	}
}
