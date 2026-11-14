package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.PersonRequest;
import com.leandro.monthly_budget.model.dtos.responses.PersonResponse;
import com.leandro.monthly_budget.model.entities.Person;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper{
	Person toEntity(PersonRequest request);
	PersonResponse toResponse(Person tag);
}