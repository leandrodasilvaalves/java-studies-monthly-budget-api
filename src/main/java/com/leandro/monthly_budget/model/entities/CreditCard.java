package com.leandro.monthly_budget.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="creditcards")
public class CreditCard extends EntityBase{
	
	@Column(nullable = false, length = 60, unique = true)
	private String name;
	
	@Column(nullable = false)
	private int dueDay;	
	
	boolean active = true;
}
