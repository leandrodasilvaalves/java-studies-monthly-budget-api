package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.leandro.monthly_budget.model.enums.CreditCardBillStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "credit_card_bills")
public class CreditCardBill extends EntityBase {

	@ManyToOne(optional = false)
	@JoinColumn(name = "credit_card_id", nullable = false)
	private CreditCard creditcard;

	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	@OneToMany(mappedBy = "creditCardBill")
	private List<CreditCardTransaction> transactions;

	@ManyToMany
	@JoinTable(
		name = "credit_card_bill_subscriptions",
		joinColumns = @JoinColumn(name = "credit_card_bill_id"),
		inverseJoinColumns = @JoinColumn(name = "subscription_id")
	)
	private List<CreditCardSubscription> subscriptions;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CreditCardBillStatus status;

	@Column(name = "paid_date")
	private LocalDate paidDate;

	@ManyToOne
	@JoinColumn(name = "paid_from_account_id")
	private Account paidFromAccount;
	
	
	public BigDecimal getAmount() {
		BigDecimal transactionsTotal = transactions == null ? BigDecimal.ZERO
				: transactions.stream()
						.filter(transaction -> !transaction.isIgnore())
						.map(Transaction::getAmount)
						.filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal subscriptionsTotal = subscriptions == null ? BigDecimal.ZERO
				: subscriptions.stream()
						.filter(CreditCardSubscription::isActive)
						.filter(subscription -> !subscription.isIgnore())
						.map(CreditCardSubscription::getAmount)
						.filter(Objects::nonNull)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

		return transactionsTotal.add(subscriptionsTotal);
	}
	
	public boolean hasEntries() {
		boolean hasTransactions = transactions != null
				&& transactions.stream().anyMatch(transaction -> !transaction.isIgnore());
		
		boolean hasSubscriptions = subscriptions != null
				&& subscriptions.stream().anyMatch(
						subscription -> subscription.isActive() && !subscription.isIgnore());

		return hasTransactions || hasSubscriptions;
	}
}
