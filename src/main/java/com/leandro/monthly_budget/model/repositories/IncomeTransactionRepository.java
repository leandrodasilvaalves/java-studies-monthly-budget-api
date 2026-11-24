package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.IncomeTransaction;

public interface IncomeTransactionRepository extends JpaRepository<IncomeTransaction, Long> {

	@Query("SELECT i FROM IncomeTransaction i WHERE i.active = :active")
	Page<IncomeTransaction> findAllByStatus(boolean active, Pageable pageable);
}
