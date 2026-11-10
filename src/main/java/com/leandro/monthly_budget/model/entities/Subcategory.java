package com.leandro.monthly_budget.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subcategories", 
	uniqueConstraints = @UniqueConstraint(
		name = "unique_subcategory_by_category", 
		columnNames = {	"name", "category_id" }
))
public class Subcategory extends EntityBase {

	@Column(nullable = false, length = 60)
	private String name;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
}
