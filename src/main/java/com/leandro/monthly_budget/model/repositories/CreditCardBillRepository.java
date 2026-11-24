package com.leandro.monthly_budget.model.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.CreditCardBill;

public interface CreditCardBillRepository extends JpaRepository<CreditCardBill, Long> {

	@Query("SELECT b FROM CreditCardBill b WHERE b.creditcard.id = :creditCardId")
	Page<CreditCardBill> findAllByCreditCardId(long creditCardId, Pageable pageable);

	@Query("SELECT b FROM CreditCardBill b WHERE b.id = :id AND b.creditcard.id = :creditCardId")
	Optional<CreditCardBill> findByIdAndCreditCardId(long id, long creditCardId);
}
