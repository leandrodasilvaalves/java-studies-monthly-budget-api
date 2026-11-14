package com.leandro.monthly_budget.model.services;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.leandro.monthly_budget.controller.mappers.ProjectMapper;
import com.leandro.monthly_budget.controller.requests.ProjectRequest;
import com.leandro.monthly_budget.controller.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Project;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.repositories.AccountRepository;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.PersonRepository;
import com.leandro.monthly_budget.model.repositories.ProjectRepository;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;
import com.leandro.monthly_budget.model.services.interfaces.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{

	private ProjectRepository projectRepository;
	private CategoryRepository categoryRepository;
	private SubcategoryRepository subcategoryRepository;
	private PersonRepository personRepository;
	private AccountRepository accountRepository;
	private ProjectMapper mapper;

	public ProjectServiceImpl(
			ProjectRepository projectRepository,
			CategoryRepository categoryRepository,
			SubcategoryRepository subcategoryRepository,
			PersonRepository personRepository,
			AccountRepository accountRepository,
			ProjectMapper mapper) {
		super();
		this.projectRepository = projectRepository;
		this.categoryRepository = categoryRepository;
		this.subcategoryRepository = subcategoryRepository;
		this.personRepository = personRepository;
		this.accountRepository = accountRepository;
		this.mapper = mapper;
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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
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

		var category = findCategory(request.getCategoryId());
		var subcategory = findSubcategory(request.getSubcategoryId(), category);
		var people = findPeople(request.getPersonIds());
		var account = findAccount(request.getAccountId());

		project.setCategory(category);
		project.setSubcategory(subcategory);
		project.setPeople(people);
		project.setAccount(account);
	}

	private void validateRequest(ProjectRequest request, Long currentId) {

		validateRequiredFields(request);
		validateDates(request);
		validateName(request.getName(), currentId);
	}

	private void validateRequiredFields(ProjectRequest request) {

		if (request == null
				|| request.getName() == null || request.getName().isBlank()
				|| request.getCategoryId() == null
				|| request.getSubcategoryId() == null
				|| request.getPersonIds() == null || request.getPersonIds().isEmpty()
				|| request.getPersonIds().stream().anyMatch(Objects::isNull)
				|| request.getAccountId() == null
				|| request.getFirstDepositDate() == null
				|| request.getGoalDate() == null
				|| request.getPlannedAmount() == null
				|| request.getType() == null) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required");
		}
	}

	private void validateDates(ProjectRequest request) {

		if (!request.getFirstDepositDate().isBefore(request.getGoalDate())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"First deposit date must be before goal date");
		}
	}

	private void validateName(String name, Long currentId) {

		boolean exists = currentId == null
				? projectRepository.existsByName(name)
				: projectRepository.existsByNameAndIdNot(name, currentId);

		if (exists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Already exists a project with this name");
		}
	}

	private Category findCategory(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
	}

	private Subcategory findSubcategory(Long id, Category category) {

		var subcategory = subcategoryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subcategory not found"));

		if (!subcategory.getCategory().getId().equals(category.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Subcategory does not belong to the informed category");
		}

		return subcategory;
	}

	private List<Person> findPeople(List<Long> personIds) {

		var ids = personIds.stream().distinct().toList();
		var people = personRepository.findAllById(ids);

		if (people.size() != ids.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"One or more people were not found");
		}

		return people;
	}

	private Account findAccount(Long id) {
		return accountRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
	}
}
