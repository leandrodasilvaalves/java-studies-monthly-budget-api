package com.leandro.monthly_budget.model.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.leandro.monthly_budget.model.enums.CreditCardTransactionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
	name = "credit_card_transactions",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_credit_card_purchase_installment",
		columnNames = { "purchase_id", "installment_number" }
	)
)
@DiscriminatorValue("CREDIT_CARD")
@PrimaryKeyJoinColumn(name = "transaction_id")
public class CreditCardTransaction extends Transaction {

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "credit_card_bill_id", nullable = false)
	private CreditCardBill creditCardBill;

	@Column(length = 80)
	private String store;

	@ManyToMany
	@JoinTable(
		name = "credit_card_transaction_people",
		joinColumns = @JoinColumn(name = "transaction_id"),
		inverseJoinColumns = @JoinColumn(name = "person_id")
	)
	private List<Person> people;

	@Min(1)
	@Column(name = "total_installments", nullable = false)
	private int totalInstallments;

	@Min(1)
	@Column(name = "installment_number", nullable = false)
	private int installmentNumber;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CreditCardTransactionStatus status;

	@NotNull
	@Column(name = "purchase_id", nullable = false)
	private UUID purchaseId;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "paid_date")
	private LocalDate paidDate;

	@AssertTrue(message = "PAID credit card transactions require paidDate and account; PENDING ones must not have them")
	public boolean isPaymentDataConsistent() {
		if (status == CreditCardTransactionStatus.PAID) {
			return paidDate != null && account != null;
		}

		if (status == CreditCardTransactionStatus.PENDING) {
			return paidDate == null && account == null;
		}

		return true;
	}

	@AssertTrue(message = "installmentNumber must be between 1 and totalInstallments")
	public boolean isInstallmentDataConsistent() {
		return installmentNumber >= 1 && installmentNumber <= totalInstallments;
	}
}
