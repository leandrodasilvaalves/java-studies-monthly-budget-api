package com.leandro.monthly_budget.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="tags")
public class Tag extends EntityBase{
		
	public Tag(String name) {
		super();
		this.name = name;
	}

	@Column(nullable = false, unique = true, length = 50)
	private String name;	
}
