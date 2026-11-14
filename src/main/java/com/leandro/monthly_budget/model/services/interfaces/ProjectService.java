package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.ProjectRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Project;

public interface ProjectService {
	
	Page<Project> getAll(int page, int size, StatusRequest status);
	Project getById(long id);
	Project create(ProjectRequest request);
	Project update(long id, ProjectRequest request);
	Project activate(long id);
	Project inactivate(long id);
}
