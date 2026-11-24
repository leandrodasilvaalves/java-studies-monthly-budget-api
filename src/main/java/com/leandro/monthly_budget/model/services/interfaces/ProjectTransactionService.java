package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.ProjectTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.ProjectTransaction;

public interface ProjectTransactionService {

	Page<ProjectTransaction> getAll(long projectId, int page, int size, StatusRequest status);
	ProjectTransaction getById(long projectId, long id);
	ProjectTransaction create(long projectId, ProjectTransactionRequest request);
	ProjectTransaction update(long projectId, long id, ProjectTransactionRequest request);
	ProjectTransaction activate(long projectId, long id);
	ProjectTransaction inactivate(long projectId, long id);
}
