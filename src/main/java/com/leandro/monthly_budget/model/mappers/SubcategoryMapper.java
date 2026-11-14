package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.SubcategoryRequest;
import com.leandro.monthly_budget.model.dtos.responses.SubcategoryResponse;
import com.leandro.monthly_budget.model.entities.Subcategory;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubcategoryMapper {	
	
	Subcategory toEntity(SubcategoryRequest request);
	
	SubcategoryResponse toResponse(Subcategory subcategory);
}