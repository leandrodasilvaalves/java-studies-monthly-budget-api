package com.leandro.monthly_budget.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class CreditCardBillAmountTest {

	@Test
	void amountIsZeroWhenBillHasNoTransactionsNorSubscriptions() {
		var bill = new CreditCardBill();

		assertThat(bill.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(bill.hasEntries()).isFalse();
	}

	@Test
	void amountSumsTransactionsAndActiveSubscriptions() {
		var bill = new CreditCardBill();
		bill.setTransactions(List.of(transaction("100.00"), transaction("50.00")));

		var activeSubscription = subscription("30.00", true, false);
		var inactiveSubscription = subscription("999.00", false, false);
		bill.setSubscriptions(List.of(activeSubscription, inactiveSubscription));

		assertThat(bill.getAmount()).isEqualByComparingTo("180.00");
		assertThat(bill.hasEntries()).isTrue();
	}

	@Test
	void amountIgnoresIgnoredTransactionsAndSubscriptions() {
		var bill = new CreditCardBill();

		var ignoredTransaction = transaction("500.00");
		ignoredTransaction.setIgnore(true);
		bill.setTransactions(List.of(transaction("20.00"), ignoredTransaction));

		var ignoredSubscription = subscription("700.00", true, true);
		bill.setSubscriptions(List.of(subscription("10.00", true, false), ignoredSubscription));

		assertThat(bill.getAmount()).isEqualByComparingTo("30.00");
	}

	@Test
	void hasEntriesIsFalseWhenOnlyIgnoredOrInactiveEntriesExist() {
		var bill = new CreditCardBill();

		var ignoredTransaction = transaction("500.00");
		ignoredTransaction.setIgnore(true);
		bill.setTransactions(List.of(ignoredTransaction));
		bill.setSubscriptions(List.of(subscription("700.00", false, false)));

		assertThat(bill.hasEntries()).isFalse();
		assertThat(bill.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	private static CreditCardTransaction transaction(String amount) {
		var transaction = new CreditCardTransaction();
		transaction.setAmount(new BigDecimal(amount));

		return transaction;
	}

	private static CreditCardSubscription subscription(
			String amount, boolean active, boolean ignore) {
		var subscription = new CreditCardSubscription();
		subscription.setAmount(new BigDecimal(amount));
		subscription.setActive(active);
		subscription.setIgnore(ignore);

		return subscription;
	}
}
