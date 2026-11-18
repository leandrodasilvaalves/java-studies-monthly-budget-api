package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.CategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.CategoryMapper;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.services.interfaces.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper mapper;

	public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper mapper) {
		this.categoryRepository = categoryRepository;
		this.mapper = mapper;
	}

	public Page<Category> getAll(int page, int size, CategoryType type, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> (type == null)
					? categoryRepository.findAll(pageable)
					: categoryRepository.findByType(type, pageable);
			case ACTIVE -> (type == null)
					? categoryRepository.findAllByStatus(true, pageable)
					: categoryRepository.findByTypeAndStatus(type, true, pageable);
			case INACTIVE -> (type == null)
					? categoryRepository.findAllByStatus(false, pageable)
					: categoryRepository.findByTypeAndStatus(type, false, pageable);
		};
	}

	public Category getById(long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
	}

	public Category create(CategoryRequest request) {

		if (categoryRepository.existsByNameAndType(request.getName(), request.getType())) {
			throw new DuplicateResourceException(
					"Already exists a category with this name and type");
		}

		return categoryRepository.save(mapper.toEntity(request));
	}

	public Category update(long id, CategoryRequest request) {

		var category = getById(id);
		category.setName(request.getName());
		category.setType(request.getType());

		return categoryRepository.save(category);
	}

	public Category activate(long id) {

		var category = getById(id);
		category.setActive(true);

		return categoryRepository.save(category);
	}

	public Category inactivate(long id) {

		var category = getById(id);
		category.setActive(false);

		return categoryRepository.save(category);
	}
}
