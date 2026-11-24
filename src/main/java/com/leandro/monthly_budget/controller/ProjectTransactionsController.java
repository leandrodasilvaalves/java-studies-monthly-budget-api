package com.leandro.monthly_budget.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leandro.monthly_budget.model.dtos.requests.ProjectTransactionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.ProjectTransactionResponse;
import com.leandro.monthly_budget.model.mappers.ProjectTransactionMapper;
import com.leandro.monthly_budget.model.services.interfaces.ProjectTransactionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/projects/{projectId}/transactions")
public class ProjectTransactionsController {

	private final ProjectTransactionService service;
	private final ProjectTransactionMapper mapper;

	public ProjectTransactionsController(
			ProjectTransactionService service,
			ProjectTransactionMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<ProjectTransactionResponse> getAll(
			@PathVariable @Min(1) long projectId,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
			@RequestParam(defaultValue = "ALL") StatusRequest status) {

		return service.getAll(projectId, page, size, status).map(mapper::toResponse);
	}

	@GetMapping("/{id}")
	public ProjectTransactionResponse getById(
			@PathVariable @Min(1) long projectId,
			@PathVariable @Min(1) long id) {

		return mapper.toResponse(service.getById(projectId, id));
	}

	@PostMapping
	public ResponseEntity<ProjectTransactionResponse> create(
			@PathVariable @Min(1) long projectId,
			@Valid @RequestBody ProjectTransactionRequest request) {

		var created = service.create(projectId, request);

		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjectTransactionResponse> update(
			@PathVariable @Min(1) long projectId,
			@PathVariable @Min(1) long id,
			@Valid @RequestBody ProjectTransactionRequest request) {

		var updated = service.update(projectId, id, request);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/activate")
	public ResponseEntity<ProjectTransactionResponse> activate(
			@PathVariable @Min(1) long projectId,
			@PathVariable @Min(1) long id) {

		var updated = service.activate(projectId, id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<ProjectTransactionResponse> inactivate(
			@PathVariable @Min(1) long projectId,
			@PathVariable @Min(1) long id) {

		var updated = service.inactivate(projectId, id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}
}
