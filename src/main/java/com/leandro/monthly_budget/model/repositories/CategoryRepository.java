package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.enums.CategoryType;

public interface CategoryRepository extends JpaRepository<Category, Long>{
	
	@Query("SELECT c FROM Category c WHERE c.type = :type")
	Page<Category> findByType(CategoryType type, Pageable pageable);
	
	@Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.type = :type")
	boolean existsByNameAndType(String name, CategoryType type);
}