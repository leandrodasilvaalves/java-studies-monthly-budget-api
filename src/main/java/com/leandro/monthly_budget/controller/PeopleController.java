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

import com.leandro.monthly_budget.model.dtos.requests.PersonRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.PersonResponse;
import com.leandro.monthly_budget.model.mappers.PersonMapper;
import com.leandro.monthly_budget.model.repositories.PersonRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/people")
public class PeopleController {
	
	private PersonRepository repository;
	private PersonMapper mapper;
	
	public PeopleController(PersonRepository repository, PersonMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@GetMapping
	public Page<PersonResponse> getAll(
			@RequestParam(defaultValue = "1") @Min(1) int page, 
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size, 
			@RequestParam(defaultValue="ALL") StatusRequest status){
		
		Pageable pageable = PageRequest.of(page -1, size);
		
		var people = status == StatusRequest.ALL
				? repository.findAll(pageable)
				: repository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
		
		return people.map(mapper::toResponse);		
	}
	
	@GetMapping("/{id}")
	public PersonResponse getById(@PathVariable long id) {
		return repository.findById(id)
				.map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Person not found")); 
	}
	
	@PostMapping
	public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request){
		
		if(repository.existsByName(request.getName())) {
			throw new DuplicateResourceException("Already exists a person with this name");
		}
		
		var entity = mapper.toEntity(request);
		var saved = repository.save(entity);
		
		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(saved));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<PersonResponse> update(@PathVariable @Min(1) long id, 
			@Valid @RequestBody PersonRequest request){
		
		return repository.findById(id)
				.map(record ->{					
					record.setName(request.getName());
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElseThrow(() -> new ResourceNotFoundException("Person not found"));
				
	}
	
	@PatchMapping("/{id}/activate")
	public ResponseEntity<PersonResponse> activate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(true);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElseThrow(() -> new ResourceNotFoundException("Person not found"));
				
	}
	
	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<PersonResponse> inactivate(@PathVariable long id){
		
		return repository.findById(id)
				.map(record ->{					
					record.setActive(false);
					var updated = repository.save(record);					
					return ResponseEntity.ok().body(mapper.toResponse(updated));
				})
				.orElseThrow(() -> new ResourceNotFoundException("Person not found"));
				
	}
}













