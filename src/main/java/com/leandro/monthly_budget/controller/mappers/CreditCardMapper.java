package com.leandro.monthly_budget.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.controller.requests.CreditCardRequest;
import com.leandro.monthly_budget.controller.responses.CreditCardResponse;
import com.leandro.monthly_budget.model.entities.CreditCard;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditCardMapper {
	
	CreditCard toEntity(CreditCardRequest request);
	CreditCardResponse toResponse(CreditCard entity);
	
}
