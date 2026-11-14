package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.FixedIncomeRequest;
import com.leandro.monthly_budget.model.dtos.responses.FixedIncomeResponse;
import com.leandro.monthly_budget.model.entities.FixedIncome;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FixedIncomeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "recipient", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "active", ignore = true)
	FixedIncome toEntity(FixedIncomeRequest request);

	FixedIncomeResponse toResponse(FixedIncome fixedIncome);
}
