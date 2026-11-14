package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>{

	@Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.name = :name")
	boolean existsByName(String name);

	@Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.name = :name AND p.id <> :id")
	boolean existsByNameAndIdNot(String name, Long id);

	@Query("SELECT p FROM Project p WHERE p.active = :active")
	Page<Project> findAllByStatus(boolean active, Pageable pageable);
}
