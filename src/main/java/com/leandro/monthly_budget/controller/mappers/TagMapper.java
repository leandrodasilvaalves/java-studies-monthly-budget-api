package com.leandro.monthly_budget.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.controller.requests.TagRequest;
import com.leandro.monthly_budget.controller.responses.TagResponse;
import com.leandro.monthly_budget.model.entities.Tag;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper{
	Tag toEntity(TagRequest request);
	TagResponse toResponse(Tag tag);
} 