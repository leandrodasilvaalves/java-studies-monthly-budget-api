package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.FixedIncome;

public interface FixedIncomeRepository extends JpaRepository<FixedIncome, Long> {

	@Query("SELECT f FROM FixedIncome f WHERE f.active = :active")
	Page<FixedIncome> findAllByStatus(boolean active, Pageable pageable);
}
