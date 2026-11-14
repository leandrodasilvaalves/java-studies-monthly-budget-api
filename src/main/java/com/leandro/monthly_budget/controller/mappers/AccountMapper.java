package com.leandro.monthly_budget.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.controller.requests.AccountCreateRequest;
import com.leandro.monthly_budget.controller.requests.AccountUpdateRequest;
import com.leandro.monthly_budget.controller.responses.AccountResponse;
import com.leandro.monthly_budget.model.entities.Account;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper{
	Account toEntity(AccountCreateRequest request);
	Account toEntity(AccountUpdateRequest request);
	AccountResponse toResponse(Account account);
}
