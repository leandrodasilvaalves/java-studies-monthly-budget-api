package com.leandro.monthly_budget.model.entities;

import java.util.List;

import com.leandro.monthly_budget.model.enums.CategoryType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(	name="categories",
	uniqueConstraints = @UniqueConstraint(
	name = "unique_categories_name_type",
	columnNames = {"name", "type"}
))
public class Category extends EntityBase{	
	
	
	public Category(String name, CategoryType type) {
		super();
		this.name = name;
		this.type = type;
	}

	@Column(nullable = false, length = 60)
	private String name;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CategoryType type;	
	
	@OneToMany(
		mappedBy = "category", 
		fetch = FetchType.LAZY, 
		cascade = CascadeType.ALL
	)
	private List<Subcategory> subcategories;    
	
}