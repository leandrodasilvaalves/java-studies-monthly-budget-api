package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.SubcategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Subcategory;

public interface SubcategoryService {

	Page<Subcategory> getAll(long categoryId, int page, int size, StatusRequest status);
	Subcategory getById(long categoryId, long id);
	Subcategory create(long categoryId, SubcategoryRequest request);
	Subcategory update(long categoryId, long id, SubcategoryRequest request);
	Subcategory activate(long categoryId, long id);
	Subcategory inactivate(long categoryId, long id);
}
