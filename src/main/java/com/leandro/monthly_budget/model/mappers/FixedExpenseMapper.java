package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.FixedExpenseRequest;
import com.leandro.monthly_budget.model.dtos.responses.FixedExpenseResponse;
import com.leandro.monthly_budget.model.entities.FixedExpense;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FixedExpenseMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "people", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "active", ignore = true)
	FixedExpense toEntity(FixedExpenseRequest request);

	FixedExpenseResponse toResponse(FixedExpense fixedExpense);
}
