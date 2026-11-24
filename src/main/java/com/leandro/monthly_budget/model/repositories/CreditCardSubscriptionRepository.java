package com.leandro.monthly_budget.model.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.CreditCardSubscription;

public interface CreditCardSubscriptionRepository extends JpaRepository<CreditCardSubscription, Long> {

	@Query("SELECT s FROM CreditCardSubscription s WHERE s.creditCard.id = :creditCardId")
	Page<CreditCardSubscription> findAllByCreditCardId(long creditCardId, Pageable pageable);

	@Query("SELECT s FROM CreditCardSubscription s WHERE s.creditCard.id = :creditCardId AND s.active = :active")
	Page<CreditCardSubscription> findAllByCreditCardIdAndStatus(
			long creditCardId, boolean active, Pageable pageable);

	@Query("SELECT s FROM CreditCardSubscription s WHERE s.id = :id AND s.creditCard.id = :creditCardId")
	Optional<CreditCardSubscription> findByIdAndCreditCardId(long id, long creditCardId);
}
