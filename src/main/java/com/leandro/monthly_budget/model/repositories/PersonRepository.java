package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.Person;

public interface PersonRepository extends JpaRepository<Person, Long>{
	
	@Query("SELECT COUNT(p) > 0 FROM Person p WHERE p.name = :name")
	boolean existsByName(String name);
	
	@Query("SELECT p FROM Person p WHERE p.active = :active")
	Page<Person> findAllByStatus(boolean active, Pageable pageable);
}