package com.leandro.monthly_budget.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.leandro.monthly_budget.controller.mappers.CreditCardMapper;
import com.leandro.monthly_budget.controller.requests.CreditCardRequest;
import com.leandro.monthly_budget.controller.requests.enums.StatusRequest;
import com.leandro.monthly_budget.controller.responses.CreditCardResponse;
import com.leandro.monthly_budget.model.repositories.CreditCardRepository;

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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); 
	}

	@PostMapping
	public ResponseEntity<CreditCardResponse> create(@RequestBody CreditCardRequest request){
		if(repository.existsByName(request.getName())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Already exists a credit card with this name");
		}
		
		var created = repository.save(mapper.toEntity(request));
		
		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
		
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<CreditCardResponse> update(@PathVariable @Min(1) long id, 
			@RequestBody CreditCardRequest request){
		
		return repository.findById(id)
				.map(record ->{					
					record.setName(request.getName());
					record.setDueDay(request.getDueDay());
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElse(ResponseEntity.notFound().build());
				
	}
	
	@PatchMapping("/{id}/activate")
	public ResponseEntity<CreditCardResponse> activate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(true);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElse(ResponseEntity.notFound().build());
				
	}
	
	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<CreditCardResponse> inactivate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(false);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElse(ResponseEntity.notFound().build());
				
	}
}
