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

import com.leandro.monthly_budget.model.dtos.requests.TagRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.TagResponse;
import com.leandro.monthly_budget.model.mappers.TagMapper;
import com.leandro.monthly_budget.model.repositories.TagRepository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value = "/api/v1/tags")
public class TagsController {

	private TagRepository repository;
	private TagMapper mapper;

	public TagsController(TagRepository repository, TagMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<TagResponse> getAll(
			@RequestParam @Min(1) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
			@RequestParam(defaultValue = "ALL") StatusRequest status) {
		
		Pageable pageable = PageRequest.of(page -1, size);
		
		var tags = status == StatusRequest.ALL 
				? repository.findAll(pageable)
				: repository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
		
		return tags.map(mapper::toResponse);		
	}

	@GetMapping("/{id}")
	public TagResponse getById(@PathVariable @Min(1) long id) {

		return repository.findById(id).map(mapper::toResponse)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	public ResponseEntity<TagResponse> create(@RequestBody TagRequest request) {

		if (repository.existsByName(request.getName())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Already exists a tag with this name");
		}

		var created = repository.save(mapper.toEntity(request));
		return ResponseEntity.created(null).body(mapper.toResponse(created));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<TagResponse> update(@PathVariable @Min(1) long id, 
		@RequestBody TagRequest request){
		
		return repository.findById(id)
				.map(record ->{
					
					record.setName(request.getName());
					var updated = repository.save(record);
					return ResponseEntity.ok().body(mapper.toResponse(updated));
					
				}).orElse(ResponseEntity.notFound().build());
	}	

	@PatchMapping("/{id}/activate")
	public ResponseEntity<TagResponse> activate(@PathVariable @Min(1) long id){
		
		return repository.findById(id)
				.map(record ->{
					
					record.setActive(true);
					var updated = repository.save(record);
					return ResponseEntity.ok().body(mapper.toResponse(updated));
					
				}).orElse(ResponseEntity.notFound().build());
	}
	
	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<TagResponse> inactivate(@PathVariable @Min(1) long id){
		
		return repository.findById(id)
				.map(record ->{
					
					record.setActive(false);
					var updated = repository.save(record);
					return ResponseEntity.ok().body(mapper.toResponse(updated));
					
				}).orElse(ResponseEntity.notFound().build());
	}
	
}
