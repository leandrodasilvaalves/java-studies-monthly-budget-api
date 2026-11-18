package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.CategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.enums.CategoryType;

public interface CategoryService {

	Page<Category> getAll(int page, int size, CategoryType type, StatusRequest status);
	Category getById(long id);
	Category create(CategoryRequest request);
	Category update(long id, CategoryRequest request);
	Category activate(long id);
	Category inactivate(long id);
}
