package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{
	
	@Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.name = :name")
	boolean existsByName(String name);
	
	@Query("SELECT a FROM Account a WHERE a.active = :active")
	Page<Account> findAllByStatus(boolean active, Pageable pageable);
}
