package com.leandro.monthly_budget.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.model.dtos.requests.TagRequest;
import com.leandro.monthly_budget.model.dtos.responses.TagResponse;
import com.leandro.monthly_budget.model.entities.Tag;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper{
	Tag toEntity(TagRequest request);
	TagResponse toResponse(Tag tag);
} 