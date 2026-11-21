package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@OneToMany(mappedBy = "creditCardBill")
	private List<CreditCardTransaction> transactions;

	@ManyToMany
	@JoinTable(
		name = "credit_card_bill_subscriptions",
		joinColumns = @JoinColumn(name = "credit_card_bill_id"),
		inverseJoinColumns = @JoinColumn(name = "subscription_id")
	)
	private List<CreditCardSubscriptions> subscriptions;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CreditCardBillStatus status;

	@Column(name = "paid_date")
	private LocalDate paidDate;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
}
