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
@Table(name="people")
public class Person extends EntityBase {
	
	
	public Person(String name) {
		super();
		this.name = name;
	}
	
	@Column(nullable = false, length = 80, unique = true)
	private String name;
}
