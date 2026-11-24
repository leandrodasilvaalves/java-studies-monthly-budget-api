package com.leandro.monthly_budget.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leandro.monthly_budget.model.entities.CreditCardTransaction;

public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long> {

}
