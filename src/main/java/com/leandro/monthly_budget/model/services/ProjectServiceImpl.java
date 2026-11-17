package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.ProjectRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Project;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.ProjectMapper;
import com.leandro.monthly_budget.model.repositories.ProjectRepository;
import com.leandro.monthly_budget.model.services.interfaces.ProjectService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectMapper mapper;
	private final ReferenceDataValidator validator;

	public ProjectServiceImpl(
			ProjectRepository projectRepository,
			ProjectMapper mapper,
			ReferenceDataValidator validator) {
		this.projectRepository = projectRepository;
		this.mapper = mapper;
		this.validator = validator;
	}

	public Page<Project> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return switch (status) {
			case ALL -> projectRepository.findAll(pageable);
			case ACTIVE -> projectRepository.findAllByStatus(true, pageable);
			case INACTIVE -> projectRepository.findAllByStatus(false, pageable);
		};
	}

	public Project getById(long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	public Project create(ProjectRequest request) {

		validateRequest(request, null);

		var project = mapper.toEntity(request);
		applyRelations(project, request);

		return projectRepository.save(project);
	}

	public Project update(long id, ProjectRequest request) {

		var project = getById(id);

		validateRequest(request, id);

		project.setName(request.getName());
		project.setFirstDepositDate(request.getFirstDepositDate());
		project.setGoalDate(request.getGoalDate());
		project.setPlannedAmount(request.getPlannedAmount());
		project.setType(request.getType());
		project.setBrokerageAccount(request.getBrokerageAccount());

		applyRelations(project, request);

		return projectRepository.save(project);
	}

	public Project activate(long id) {

		var project = getById(id);
		project.setActive(true);

		return projectRepository.save(project);
	}

	public Project inactivate(long id) {

		var project = getById(id);
		project.setActive(false);

		return projectRepository.save(project);
	}

	private void applyRelations(Project project, ProjectRequest request) {

		var category = validator.requireCategory(request.getCategoryId());

		project.setCategory(category);
		project.setSubcategory(
				validator.requireSubcategory(request.getSubcategoryId(), category));
		project.setPeople(validator.requirePeople(request.getPersonIds()));
		project.setAccount(validator.requireAccount(request.getAccountId()));
	}

	private void validateRequest(ProjectRequest request, Long currentId) {

		validator.requireRequest(request);
		validator.requireDateOrder(request.getFirstDepositDate(), request.getGoalDate());
		validateName(request.getName(), currentId);
	}

	private void validateName(String name, Long currentId) {

		boolean exists = currentId == null
				? projectRepository.existsByName(name)
				: projectRepository.existsByNameAndIdNot(name, currentId);

		if (exists) {
			throw new DuplicateResourceException("Already exists a project with this name");
		}
	}
}
