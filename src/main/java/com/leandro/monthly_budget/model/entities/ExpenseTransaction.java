package com.leandro.monthly_budget.model.entities;

import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ExpenseStatus;

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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "expense_transactions")
@DiscriminatorValue("EXPENSE")
@PrimaryKeyJoinColumn(name = "transaction_id")
public class ExpenseTransaction extends Transaction {

	@Column(length = 80)
	private String store;

	@ManyToMany
	@JoinTable(
		name = "expense_transaction_people",
		joinColumns = @JoinColumn(name = "transaction_id"),
		inverseJoinColumns = @JoinColumn(name = "person_id")
	)
	private List<Person> people;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ExpenseStatus status;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "paid_date")
	private LocalDate paidDate;

	@AssertTrue(message = "PAID expenses require paidDate and account; PENDING expenses must not have them")
	public boolean isPaymentDataConsistent() {
		if (status == ExpenseStatus.PAID) {
			return paidDate != null && account != null;
		}

		if (status == ExpenseStatus.PENDING) {
			return paidDate == null && account == null;
		}

		return true;
	}
}
