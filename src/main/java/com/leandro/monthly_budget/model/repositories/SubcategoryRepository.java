package com.leandro.monthly_budget.model.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.leandro.monthly_budget.model.entities.Subcategory;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long>{
	
	@Query("SELECT COUNT(s) > 0 FROM Subcategory s WHERE s.name = :name AND s.category.id = :categoryId")
	boolean existsByNameAndCategory(String name, Long categoryId);
	
	@Query("SELECT s FROM Subcategory s WHERE s.id= :id AND s.category.id = :categoryId")
	Optional<Subcategory>  findByIdAndCategoryId(Long id, Long categoryId);
	
	@Query("SELECT s FROM Subcategory s WHERE s.category.id = :categoryId ORDER BY s.id")
	Page<Subcategory> findAllByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
}