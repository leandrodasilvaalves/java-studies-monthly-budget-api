package com.leandro.monthly_budget.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.controller.requests.SubcategoryRequest;
import com.leandro.monthly_budget.controller.responses.SubcategoryResponse;
import com.leandro.monthly_budget.model.entities.Subcategory;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubcategoryMapper {	
	
	Subcategory toEntity(SubcategoryRequest request);
	
	SubcategoryResponse toResponse(Subcategory subcategory);
}