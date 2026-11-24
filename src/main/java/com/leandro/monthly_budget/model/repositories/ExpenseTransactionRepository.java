package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.ExpenseTransaction;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

	@Query("SELECT e FROM ExpenseTransaction e WHERE e.active = :active")
	Page<ExpenseTransaction> findAllByStatus(boolean active, Pageable pageable);
}
