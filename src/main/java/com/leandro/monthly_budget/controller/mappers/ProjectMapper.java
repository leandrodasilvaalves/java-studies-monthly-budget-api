package com.leandro.monthly_budget.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.leandro.monthly_budget.controller.requests.ProjectRequest;
import com.leandro.monthly_budget.controller.responses.ProjectResponse;
import com.leandro.monthly_budget.model.entities.Project;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "people", ignore = true)
	@Mapping(target = "account", ignore = true)
	@Mapping(target = "active", ignore = true)
	Project toEntity(ProjectRequest request);

	ProjectResponse toResponse(Project project);
}
