package com.leandro.monthly_budget.model.entities;

import com.leandro.monthly_budget.model.enums.CategoryType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(	name="categories",
	uniqueConstraints = @UniqueConstraint(
		name = "unique_categories_name_type",
		columnNames = {"name", "type"}
))
public class Category extends EntityBase{
	
	@NotBlank(message = "Name is required")	
	private String name;
	
	@NotNull(message="Type is required")
	@Enumerated(EnumType.STRING)
	private CategoryType type;	
    
}