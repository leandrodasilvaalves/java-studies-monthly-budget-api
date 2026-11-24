package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardTransactionRequest;
import com.leandro.monthly_budget.model.dtos.responses.CreditCardTransactionResponse;
import com.leandro.monthly_budget.model.entities.CreditCardTransaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditCardTransactionMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "creditCardBill", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "people", ignore = true)
	@Mapping(target = "tags", ignore = true)
	CreditCardTransaction toEntity(CreditCardTransactionRequest request);

	@Mapping(target = "creditCardBillId", source = "creditCardBill.id")
	CreditCardTransactionResponse toResponse(CreditCardTransaction transaction);
}
