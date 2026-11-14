package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.FixedExpense;

public interface FixedExpenseRepository extends JpaRepository<FixedExpense, Long> {

	@Query("SELECT f FROM FixedExpense f WHERE f.active = :active")
	Page<FixedExpense> findAllByStatus(boolean active, Pageable pageable);
}
