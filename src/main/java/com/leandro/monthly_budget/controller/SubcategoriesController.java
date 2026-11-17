package com.leandro.monthly_budget.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import com.leandro.monthly_budget.model.dtos.requests.SubcategoryRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.dtos.responses.SubcategoryResponse;
import com.leandro.monthly_budget.model.mappers.SubcategoryMapper;
import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.SubcategoryRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


@RestController
@RequestMapping("/api/v1/categories/{categoryId}/subcategories")
public class SubcategoriesController {
	
	private CategoryRepository categoryRepository;	
	private SubcategoryRepository subcategoryRepository;
	private SubcategoryMapper mapper;	
	
	public SubcategoriesController(
			CategoryRepository categoryRepository, 
			SubcategoryRepository subcategoryRepository,
			SubcategoryMapper mapper) {
		super();
		this.categoryRepository = categoryRepository;
		this.subcategoryRepository = subcategoryRepository;
		this.mapper = mapper;
	}	
	
	@GetMapping
	public Page<SubcategoryResponse> getAll(
			@PathVariable @Min(1)Long categoryId, 
			@RequestParam(defaultValue = "1") @Min(1) int page, 
			@RequestParam(defaultValue = "20")@Min(1) @Max(50) int size,
			@RequestParam(defaultValue = "ALL") StatusRequest status){
		
		var pageable = PageRequest.of(page -1, size);
		var subcategories = switch (status) {
			case ALL -> subcategoryRepository.findAllByCategory(categoryId, pageable);
			case ACTIVE -> subcategoryRepository.findAllByCategoryAndStatus(categoryId, true, pageable);
			case INACTIVE -> subcategoryRepository.findAllByCategoryAndStatus(categoryId, false, pageable);
		};
		return subcategories.map(mapper::toResponse);
	}
	
	@GetMapping("/{id}")
	public SubcategoryResponse getById(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id)
	{
		return subcategoryRepository.findByIdAndCategoryId(id, categoryId)
				.map(record -> mapper.toResponse(record))
				.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
	}

	@PostMapping
	public ResponseEntity<SubcategoryResponse> create(
			@PathVariable @Min(1) Long categoryId, 
			@Valid @RequestBody SubcategoryRequest request){
		
		var category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		if (subcategoryRepository.existsByNameAndCategory(request.getName(), categoryId)) {
			throw new DuplicateResourceException(
				"Already exists a subcategory with this name for this category");
		}	
		
		var subcategory = mapper.toEntity(request);
		subcategory.setCategory(category);		
		var created = subcategoryRepository.save(subcategory);
		
		return ResponseEntity
				.created(null)
				.body(mapper.toResponse(created));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<SubcategoryResponse> update(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id,
			@Valid @RequestBody SubcategoryRequest request){
		
		categoryRepository.findById(categoryId)
			.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		return subcategoryRepository.findByIdAndCategoryId(id, categoryId)
			.map(record ->{
				
				record.setName(request.getName());
				var updated = subcategoryRepository.save(record);
				return ResponseEntity.ok(mapper.toResponse(updated));				
			})
			.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
				
	}	
	
	@PatchMapping("/{id}/activate")
	public ResponseEntity<SubcategoryResponse> activate(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id){
		
		return subcategoryRepository.findByIdAndCategoryId(id, categoryId)
			.map(record -> {
				
				record.setActive(true);
				var updated = subcategoryRepository.save(record);
				return ResponseEntity.ok(mapper.toResponse(updated));				
			})
			.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
	}
	
	@PatchMapping("/{id}/inactivate")
	public ResponseEntity<SubcategoryResponse> inactivate(
			@PathVariable @Min(1) Long categoryId,
			@PathVariable @Min(1) Long id){
		
		return subcategoryRepository.findByIdAndCategoryId(id, categoryId)
			.map(record -> {
				
				record.setActive(false);
				var updated = subcategoryRepository.save(record);
				return ResponseEntity.ok(mapper.toResponse(updated));				
			})
			.orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
	}
}

