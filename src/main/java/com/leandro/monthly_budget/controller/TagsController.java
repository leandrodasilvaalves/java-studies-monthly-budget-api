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

import com.leandro.monthly_budget.model.dtos.requests.TagRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.TagResponse;
import com.leandro.monthly_budget.model.mappers.TagMapper;
import com.leandro.monthly_budget.model.services.interfaces.TagService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value = "/api/v1/tags")
public class TagsController {

	private TagService service;
	private TagMapper mapper;

	public TagsController(TagService service, TagMapper mapper) {
		super();
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<TagResponse> getAll(
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
			@RequestParam(defaultValue = "ALL") StatusRequest status) {

		return service.getAll(page, size, status).map(mapper::toResponse);
	}

	@GetMapping("/{id}")
	public TagResponse getById(@PathVariable @Min(1) long id) {
		return mapper.toResponse(service.getById(id));
	}

	@PostMapping
	public ResponseEntity<TagResponse> create(@Valid @RequestBody TagRequest request) {

		var created = service.create(request);

		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TagResponse> update(@PathVariable @Min(1) long id,
			@Valid @RequestBody TagRequest request) {

		var updated = service.update(id, request);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/activate")
	public ResponseEntity<TagResponse> activate(@PathVariable @Min(1) long id) {

		var updated = service.activate(id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<TagResponse> inactivate(@PathVariable @Min(1) long id) {

		var updated = service.inactivate(id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}
}
