package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.ExpenseTransactionRequest;
import com.leandro.monthly_budget.model.dtos.responses.ExpenseTransactionResponse;
import com.leandro.monthly_budget.model.entities.ExpenseTransaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpenseTransactionMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "people", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "account", ignore = true)
	ExpenseTransaction toEntity(ExpenseTransactionRequest request);

	ExpenseTransactionResponse toResponse(ExpenseTransaction expenseTransaction);
}
