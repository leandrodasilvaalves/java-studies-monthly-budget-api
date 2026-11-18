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

import com.leandro.monthly_budget.model.dtos.requests.PersonRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.PersonResponse;
import com.leandro.monthly_budget.model.mappers.PersonMapper;
import com.leandro.monthly_budget.model.services.interfaces.PersonService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/people")
public class PeopleController {

	private PersonService service;
	private PersonMapper mapper;

	public PeopleController(PersonService service, PersonMapper mapper) {
		super();
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<PersonResponse> getAll(
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
			@RequestParam(defaultValue = "ALL") StatusRequest status) {

		return service.getAll(page, size, status).map(mapper::toResponse);
	}

	@GetMapping("/{id}")
	public PersonResponse getById(@PathVariable long id) {
		return mapper.toResponse(service.getById(id));
	}

	@PostMapping
	public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {

		var created = service.create(request);

		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
	}

	@PutMapping("/{id}")
	public ResponseEntity<PersonResponse> update(@PathVariable @Min(1) long id,
			@Valid @RequestBody PersonRequest request) {

		var updated = service.update(id, request);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/activate")
	public ResponseEntity<PersonResponse> activate(@PathVariable long id) {

		var updated = service.activate(id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<PersonResponse> inactivate(@PathVariable long id) {

		var updated = service.inactivate(id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}
}
