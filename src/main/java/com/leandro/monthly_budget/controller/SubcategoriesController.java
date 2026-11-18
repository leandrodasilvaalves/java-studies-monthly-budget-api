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

import com.leandro.monthly_budget.model.dtos.requests.SubcategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.SubcategoryResponse;
import com.leandro.monthly_budget.model.mappers.SubcategoryMapper;
import com.leandro.monthly_budget.model.services.interfaces.SubcategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/categories/{categoryId}/subcategories")
public class SubcategoriesController {

	private SubcategoryService service;
	private SubcategoryMapper mapper;

	public SubcategoriesController(SubcategoryService service, SubcategoryMapper mapper) {
		super();
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<SubcategoryResponse> getAll(
			@PathVariable @Min(1) Long categoryId,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
			@RequestParam(defaultValue = "ALL") StatusRequest status) {

		return service.getAll(categoryId, page, size, status).map(mapper::toResponse);
	}

	@GetMapping("/{id}")
	public SubcategoryResponse getById(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id) {

		return mapper.toResponse(service.getById(categoryId, id));
	}

	@PostMapping
	public ResponseEntity<SubcategoryResponse> create(
			@PathVariable @Min(1) Long categoryId,
			@Valid @RequestBody SubcategoryRequest request) {

		var created = service.create(categoryId, request);

		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SubcategoryResponse> update(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id,
			@Valid @RequestBody SubcategoryRequest request) {

		var updated = service.update(categoryId, id, request);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/activate")
	public ResponseEntity<SubcategoryResponse> activate(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id) {

		var updated = service.activate(categoryId, id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<SubcategoryResponse> inactivate(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id) {

		var updated = service.inactivate(categoryId, id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}
}
