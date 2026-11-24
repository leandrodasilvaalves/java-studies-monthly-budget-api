package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.ProjectTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.ProjectTransaction;
import com.leandro.monthly_budget.model.enums.ProjectTransactionStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionType;
import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.ProjectTransactionMapper;
import com.leandro.monthly_budget.model.repositories.ProjectTransactionRepository;
import com.leandro.monthly_budget.model.services.interfaces.ProjectTransactionService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class ProjectTransactionServiceImpl implements ProjectTransactionService {

	private final ProjectTransactionRepository projectTransactionRepository;
	private final ProjectTransactionMapper mapper;
	private final ReferenceDataValidator validator;

	public ProjectTransactionServiceImpl(
			ProjectTransactionRepository projectTransactionRepository,
			ProjectTransactionMapper mapper,
			ReferenceDataValidator validator) {
		this.projectTransactionRepository = projectTransactionRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	public Page<ProjectTransaction> getAll(long projectId, int page, int size, StatusRequest status) {

		validator.requireProject(projectId);

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> projectTransactionRepository.findAllByProjectId(projectId, pageable);
			case ACTIVE -> projectTransactionRepository.findAllByProjectIdAndStatus(projectId, true, pageable);
			case INACTIVE -> projectTransactionRepository.findAllByProjectIdAndStatus(projectId, false, pageable);
		};
	}

	public ProjectTransaction getById(long projectId, long id) {
		return projectTransactionRepository.findByIdAndProjectId(id, projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project transaction not found"));
	}

	public ProjectTransaction create(long projectId, ProjectTransactionRequest request) {

		validateRequest(request);

		var projectTransaction = mapper.toEntity(request);
		projectTransaction.setProject(validator.requireProject(projectId));
		applyRelations(projectTransaction, request);

		return projectTransactionRepository.save(projectTransaction);
	}

	public ProjectTransaction update(long projectId, long id, ProjectTransactionRequest request) {

		var projectTransaction = getById(projectId, id);

		validateRequest(request);

		projectTransaction.setRegisteredDate(request.getRegisteredDate());
		projectTransaction.setDueDate(request.getDueDate());
		projectTransaction.setProjectTransactionType(request.getProjectTransactionType());
		projectTransaction.setStatus(request.getStatus());
		projectTransaction.setMovementDate(request.getMovementDate());
		projectTransaction.setDescription(request.getDescription());
		projectTransaction.setAmount(request.getAmount());

		if (request.getIgnore() != null) {
			projectTransaction.setIgnore(request.getIgnore());
		}

		projectTransaction.setNotes(request.getNotes());

		applyRelations(projectTransaction, request);

		return projectTransactionRepository.save(projectTransaction);
	}

	public ProjectTransaction activate(long projectId, long id) {

		var projectTransaction = getById(projectId, id);
		projectTransaction.setActive(true);

		return projectTransactionRepository.save(projectTransaction);
	}

	public ProjectTransaction inactivate(long projectId, long id) {

		var projectTransaction = getById(projectId, id);
		projectTransaction.setActive(false);

		return projectTransactionRepository.save(projectTransaction);
	}

	private void validateRequest(ProjectTransactionRequest request) {

		validator.requireRequest(request);

		if (request.getStatus() == ProjectTransactionStatus.DONE
				&& request.getMovementDate() == null) {
			throw new BusinessRuleException("DONE project transactions require movementDate");
		}

		if (request.getStatus() == ProjectTransactionStatus.PENDING
				&& (request.getDueDate() == null || request.getMovementDate() != null)) {
			throw new BusinessRuleException(
					"PENDING project transactions require dueDate and must not have movementDate");
		}

		if (request.getProjectTransactionType() == ProjectTransactionType.YIELD
				&& request.getAccountId() != null) {
			throw new BusinessRuleException("YIELD project transactions must not have an account");
		}

		if ((request.getProjectTransactionType() == ProjectTransactionType.DEPOSIT
				|| request.getProjectTransactionType() == ProjectTransactionType.WITHDRAWAL)
				&& request.getAccountId() == null) {
			throw new BusinessRuleException(
					"DEPOSIT and WITHDRAWAL project transactions require an account");
		}
	}

	private void applyRelations(ProjectTransaction projectTransaction, ProjectTransactionRequest request) {

		var category = validator.requireCategory(request.getCategoryId());

		projectTransaction.setCategory(category);
		projectTransaction.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		projectTransaction.setPeople(validator.requirePeople(request.getPersonIds()));
		projectTransaction.setTags(validator.requireTags(request.getTagIds()));
		projectTransaction.setAccount(
				request.getAccountId() == null ? null : validator.requireAccount(request.getAccountId()));
	}
}
