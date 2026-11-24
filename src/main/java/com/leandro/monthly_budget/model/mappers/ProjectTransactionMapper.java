package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.ProjectTransactionRequest;
import com.leandro.monthly_budget.model.dtos.responses.ProjectTransactionResponse;
import com.leandro.monthly_budget.model.entities.ProjectTransaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectTransactionMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "project", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "people", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "account", ignore = true)
	ProjectTransaction toEntity(ProjectTransactionRequest request);

	ProjectTransactionResponse toResponse(ProjectTransaction projectTransaction);
}
