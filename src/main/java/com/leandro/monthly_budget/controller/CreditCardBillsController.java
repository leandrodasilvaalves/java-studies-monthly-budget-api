package com.leandro.monthly_budget.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leandro.monthly_budget.model.dtos.responses.CreditCardBillResponse;
import com.leandro.monthly_budget.model.mappers.CreditCardBillMapper;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardBillService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/creditcards/{creditCardId}/bills")
public class CreditCardBillsController {

	private final CreditCardBillService service;
	private final CreditCardBillMapper mapper;

	public CreditCardBillsController(
			CreditCardBillService service, CreditCardBillMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<CreditCardBillResponse> getAll(
			@PathVariable @Min(1) long creditCardId,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {

		return service.getAll(creditCardId, page, size).map(mapper::toResponse);
	}

	@GetMapping("/{id}")
	public CreditCardBillResponse getById(
			@PathVariable @Min(1) long creditCardId,
			@PathVariable @Min(1) long id) {

		return mapper.toResponse(service.getById(creditCardId, id));
	}
}
