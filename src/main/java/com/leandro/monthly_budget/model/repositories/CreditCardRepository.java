package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.CreditCard;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
	
	@Query("SELECT COUNT(c) > 0 FROM CreditCard c WHERE c.name = :name")
	boolean existsByName(String name);
	
	@Query("SELECT c FROM CreditCard c WHERE c.active = :active")
	Page<CreditCard> findAllByStatus(boolean active, Pageable pageable);

}
