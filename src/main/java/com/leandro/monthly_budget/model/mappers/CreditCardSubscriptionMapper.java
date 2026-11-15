package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardSubscriptionRequest;
import com.leandro.monthly_budget.model.dtos.responses.CreditCardSubscriptionResponse;
import com.leandro.monthly_budget.model.entities.CreditCardSubscriptions;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditCardSubscriptionMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "creditCard", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "people", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "active", ignore = true)
	CreditCardSubscriptions toEntity(CreditCardSubscriptionRequest request);

	CreditCardSubscriptionResponse toResponse(CreditCardSubscriptions subscription);
}
