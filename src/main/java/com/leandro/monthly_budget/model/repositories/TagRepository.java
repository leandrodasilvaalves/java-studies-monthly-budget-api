package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query("SELECT COUNT(t) > 0 FROM Tag t WHERE t.name = :name")
	boolean existsByName(String name);

	@Query("SELECT t FROM Tag t WHERE t.active = :active")
	Page<Tag> findAllByStatus(boolean active, Pageable pageable);
}