package com.leandro.monthly_budget.model.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.CreditCardTransaction;

public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long> {

	@Query("SELECT t FROM CreditCardTransaction t WHERE t.creditCardBill.creditcard.id = :creditCardId")
	Page<CreditCardTransaction> findAllByCreditCardId(long creditCardId, Pageable pageable);

	@Query("""
			SELECT t FROM CreditCardTransaction t
			WHERE t.creditCardBill.creditcard.id = :creditCardId
			  AND t.active = :active
			""")
	Page<CreditCardTransaction> findAllByCreditCardIdAndStatus(
			long creditCardId, boolean active, Pageable pageable);

	@Query("""
			SELECT t FROM CreditCardTransaction t
			WHERE t.id = :id
			  AND t.creditCardBill.creditcard.id = :creditCardId
			""")
	Optional<CreditCardTransaction> findByIdAndCreditCardId(long id, long creditCardId);
}
