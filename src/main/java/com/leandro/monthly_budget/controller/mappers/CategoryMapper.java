package com.leandro.monthly_budget.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.controller.requests.CategoryRequest;
import com.leandro.monthly_budget.controller.responses.CategoryResponse;
import com.leandro.monthly_budget.model.entities.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

	Category toEntity(CategoryRequest request);

	CategoryResponse toResponse(Category category);
}
