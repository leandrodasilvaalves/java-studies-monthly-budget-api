package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.AccountCreateRequest;
import com.leandro.monthly_budget.model.dtos.requests.AccountUpdateRequest;
import com.leandro.monthly_budget.model.dtos.responses.AccountResponse;
import com.leandro.monthly_budget.model.entities.Account;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper{
	Account toEntity(AccountCreateRequest request);
	Account toEntity(AccountUpdateRequest request);
	AccountResponse toResponse(Account account);
}
