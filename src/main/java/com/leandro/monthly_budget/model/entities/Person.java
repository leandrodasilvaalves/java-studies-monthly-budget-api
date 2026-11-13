package com.leandro.monthly_budget.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="people")
public class Person extends EntityBase {
	
	
	public Person(String name) {
		super();
		this.name = name;
	}
	
	@Column(length = 80, unique = true)
	private String name;

	private boolean active = true;
}
