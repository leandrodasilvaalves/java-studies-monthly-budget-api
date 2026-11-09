package com.leandro.monthly_budget.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {
	
	private CategoryRepository repository;
	
		
	public CategoriesController(CategoryRepository repository) {
		super();
		this.repository = repository;
	}
	
	@GetMapping	
	public Page<Category> getAll(
			@RequestParam(defaultValue = "1") @Min(1) int page, 
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
			@RequestParam(required=false) CategoryType type){
		
		Pageable pageable = PageRequest.of(page - 1, size);
		return (type ==  null)
				? repository.findAll(pageable)
				: repository.findByType(type, pageable);
	}
	
	@GetMapping("/{id}")
	public Category getById(
			@PathVariable @Min(1) long id){
		
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Category create(@Valid	 @RequestBody Category entity) {		
		
		if(repository.existsByNameAndType(entity.getName(), entity.getType())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Already exists a category with this name and type"); 
		}			
			
		var saved = repository.save(entity);
		return saved;
	}
}
