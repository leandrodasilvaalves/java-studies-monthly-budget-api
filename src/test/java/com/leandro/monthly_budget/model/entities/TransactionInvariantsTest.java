package com.leandro.monthly_budget.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.leandro.monthly_budget.model.enums.ExpenseStatus;
import com.leandro.monthly_budget.model.enums.IncomeStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionType;

class TransactionInvariantsTest {

	@Test
	void paidExpenseRequiresDateAndAccount() {
		var expense = new ExpenseTransaction();
		expense.setStatus(ExpenseStatus.PAID);

		assertThat(expense.isPaymentDataConsistent()).isFalse();

		expense.setPaidDate(LocalDate.now());
		assertThat(expense.isPaymentDataConsistent()).isFalse();

		expense.setAccount(new Account("Nubank", java.math.BigDecimal.TEN));
		assertThat(expense.isPaymentDataConsistent()).isTrue();
	}

	@Test
	void pendingExpenseMustNotHavePaymentData() {
		var expense = new ExpenseTransaction();
		expense.setStatus(ExpenseStatus.PENDING);
		expense.setPaidDate(LocalDate.now());

		assertThat(expense.isPaymentDataConsistent()).isFalse();
	}

	@Test
	void receivedIncomeRequiresDateAndAccount() {
		var income = new IncomeTransaction();
		income.setStatus(IncomeStatus.RECEIVED);
		income.setReceivedDate(LocalDate.now());
		income.setAccount(new Account("Nubank", java.math.BigDecimal.TEN));

		assertThat(income.isReceiptDataConsistent()).isTrue();
	}

	@Test
	void doneProjectTransactionRequiresMovementDate() {
		var transaction = new ProjectTransaction();
		transaction.setStatus(ProjectTransactionStatus.DONE);

		assertThat(transaction.isProjectDateConsistent()).isFalse();

		transaction.setMovementDate(LocalDate.now());
		assertThat(transaction.isProjectDateConsistent()).isTrue();
	}

	@Test
	void yieldProjectTransactionMustNotHaveAccount() {
		var transaction = new ProjectTransaction();
		transaction.setProjectTransactionType(ProjectTransactionType.YIELD);
		transaction.setAccount(new Account("Nubank", java.math.BigDecimal.TEN));

		assertThat(transaction.isProjectAccountConsistent()).isFalse();

		transaction.setAccount(null);
		assertThat(transaction.isProjectAccountConsistent()).isTrue();
	}
}
