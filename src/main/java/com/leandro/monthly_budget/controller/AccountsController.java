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

import com.leandro.monthly_budget.model.dtos.requests.AccountCreateRequest;
import com.leandro.monthly_budget.model.dtos.requests.AccountUpdateRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.AccountResponse;
import com.leandro.monthly_budget.model.mappers.AccountMapper;
import com.leandro.monthly_budget.model.repositories.AccountRepository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountsController {
	
	private AccountRepository repository;
	private AccountMapper mapper;
	
	public AccountsController(AccountRepository repository, AccountMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@GetMapping
	public Page<AccountResponse> getAll(
			@RequestParam(defaultValue = "1") @Min(1) int page, 
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size, 
			@RequestParam(defaultValue="ALL") StatusRequest status){
		
		Pageable pageable = PageRequest.of(page -1, size);
		
		var accounts = status == StatusRequest.ALL
				? repository.findAll(pageable)
				: repository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
		
		return accounts.map(mapper::toResponse);		
	}
	
	@GetMapping("/{id}")
	public AccountResponse getById(@PathVariable long id) {
		return repository.findById(id)
				.map(mapper::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); 
	}
	
	@PostMapping
	public ResponseEntity<AccountResponse> create(@RequestBody AccountCreateRequest request){
		
		if(repository.existsByName(request.getName())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Already exists an account with this name");
		}
		
		var entity = mapper.toEntity(request);
		var saved = repository.save(entity);
		
		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(saved));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<AccountResponse> update(@PathVariable @Min(1) long id, 
			@RequestBody AccountUpdateRequest request){
		
		return repository.findById(id)
				.map(record ->{					
					record.setName(request.getName());					
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElse(ResponseEntity.notFound().build());
				
	}
	
	@PatchMapping("/{id}/activate")
	public ResponseEntity<AccountResponse> activate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(true);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElse(ResponseEntity.notFound().build());
				
	}
	
	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<AccountResponse> inactivate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(false);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElse(ResponseEntity.notFound().build());
				
	}
}
