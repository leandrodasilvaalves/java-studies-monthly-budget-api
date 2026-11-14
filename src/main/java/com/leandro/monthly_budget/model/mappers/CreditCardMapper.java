package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardRequest;
import com.leandro.monthly_budget.model.dtos.responses.CreditCardResponse;
import com.leandro.monthly_budget.model.entities.CreditCard;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditCardMapper {
	
	CreditCard toEntity(CreditCardRequest request);
	CreditCardResponse toResponse(CreditCard entity);
	
}
