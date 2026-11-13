package com.leandro.monthly_budget.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.controller.requests.PersonRequest;
import com.leandro.monthly_budget.controller.responses.PersonResponse;
import com.leandro.monthly_budget.model.entities.Person;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper{
	Person toEntity(PersonRequest request);
	PersonResponse toResponse(Person tag);
}