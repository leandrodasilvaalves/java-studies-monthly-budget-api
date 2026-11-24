package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.IncomeTransactionRequest;
import com.leandro.monthly_budget.model.dtos.responses.IncomeTransactionResponse;
import com.leandro.monthly_budget.model.entities.IncomeTransaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IncomeTransactionMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "account", ignore = true)
	IncomeTransaction toEntity(IncomeTransactionRequest request);

	IncomeTransactionResponse toResponse(IncomeTransaction incomeTransaction);
}
