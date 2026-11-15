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

import com.leandro.monthly_budget.model.dtos.requests.CreditCardSubscriptionRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.CreditCardSubscriptionResponse;
import com.leandro.monthly_budget.model.mappers.CreditCardSubscriptionMapper;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardSubscriptionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/creditcards/{creditCardId}/subscriptions")
public class CreditCardSubscriptionsController {

	private CreditCardSubscriptionService service;
	private CreditCardSubscriptionMapper mapper;

	public CreditCardSubscriptionsController(
			CreditCardSubscriptionService service, CreditCardSubscriptionMapper mapper) {
		super();
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<CreditCardSubscriptionResponse> getAll(
			@PathVariable @Min(1) long creditCardId,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
			@RequestParam(defaultValue = "ALL") StatusRequest status) {

		return service.getAll(creditCardId, page, size, status).map(mapper::toResponse);
	}

	@GetMapping("/{id}")
	public CreditCardSubscriptionResponse getById(
			@PathVariable @Min(1) long creditCardId,
			@PathVariable @Min(1) long id) {

		return mapper.toResponse(service.getById(creditCardId, id));
	}

	@PostMapping
	public ResponseEntity<CreditCardSubscriptionResponse> create(
			@PathVariable @Min(1) long creditCardId,
			@Valid @RequestBody CreditCardSubscriptionRequest request) {

		var created = service.create(creditCardId, request);

		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CreditCardSubscriptionResponse> update(
			@PathVariable @Min(1) long creditCardId,
			@PathVariable @Min(1) long id,
			@Valid @RequestBody CreditCardSubscriptionRequest request) {

		var updated = service.update(creditCardId, id, request);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/activate")
	public ResponseEntity<CreditCardSubscriptionResponse> activate(
			@PathVariable @Min(1) long creditCardId,
			@PathVariable @Min(1) long id) {

		var updated = service.activate(creditCardId, id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}

	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<CreditCardSubscriptionResponse> inactivate(
			@PathVariable @Min(1) long creditCardId,
			@PathVariable @Min(1) long id) {

		var updated = service.inactivate(creditCardId, id);

		return ResponseEntity.ok().body(mapper.toResponse(updated));
	}
}
