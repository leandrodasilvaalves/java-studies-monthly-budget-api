package com.leandro.monthly_budget.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;

import com.leandro.monthly_budget.model.dtos.requests.CreditCardRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.CreditCardResponse;
import com.leandro.monthly_budget.model.mappers.CreditCardMapper;
import com.leandro.monthly_budget.model.repositories.CreditCardRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/creditcards")
public class CreditCardsController {
	
	private CreditCardRepository repository;
	private CreditCardMapper mapper;	
	
	
	public CreditCardsController(CreditCardRepository repository, CreditCardMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<CreditCardResponse> getAll(
			@RequestParam(defaultValue = "1") @Min(1) int page, 
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size, 
			@RequestParam(defaultValue="ALL") StatusRequest status){
		
		Pageable pageable = PageRequest.of(page -1, size);
		
		var creditCards = status == StatusRequest.ALL
				? repository.findAll(pageable)
				: repository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
		
		return creditCards.map(mapper::toResponse);
	}
	
	@GetMapping("/{id}")
	public CreditCardResponse getById(@PathVariable long id) {
		return repository.findById(id)
				.map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card not found")); 
	}

	@PostMapping
	public ResponseEntity<CreditCardResponse> create(@Valid @RequestBody CreditCardRequest request){
		if(repository.existsByName(request.getName())) {
			throw new DuplicateResourceException("Already exists a credit card with this name");
		}
		
		var created = repository.save(mapper.toEntity(request));
		
		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
		
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<CreditCardResponse> update(@PathVariable @Min(1) long id, 
			@Valid @RequestBody CreditCardRequest request){
		
		return repository.findById(id)
				.map(record ->{					
					record.setName(request.getName());
					record.setDueDay(request.getDueDay());
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
				
	}
	
	@PatchMapping("/{id}/activate")
	public ResponseEntity<CreditCardResponse> activate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(true);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
				
	}
	
	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<CreditCardResponse> inactivate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(false);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
				
	}
}
