package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.CategoryRequest;
import com.leandro.monthly_budget.model.dtos.responses.CategoryResponse;
import com.leandro.monthly_budget.model.entities.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

	Category toEntity(CategoryRequest request);

	CategoryResponse toResponse(Category category);
}
