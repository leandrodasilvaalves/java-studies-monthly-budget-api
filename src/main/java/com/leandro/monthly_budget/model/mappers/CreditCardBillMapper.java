package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.responses.CreditCardBillResponse;
import com.leandro.monthly_budget.model.entities.CreditCardBill;

@Mapper(
		componentModel = MappingConstants.ComponentModel.SPRING,
		uses = { CreditCardMapper.class, AccountMapper.class })
public interface CreditCardBillMapper {

	@Mapping(target = "creditCard", source = "creditcard")
	@Mapping(target = "hasEntries", expression = "java(bill.hasEntries())")
	CreditCardBillResponse toResponse(CreditCardBill bill);
}
