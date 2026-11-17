package com.leandro.monthly_budget.model.entities;

import java.time.LocalDate;

import com.leandro.monthly_budget.model.enums.IncomeStatus;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "income_transactions")
@DiscriminatorValue("INCOME")
@PrimaryKeyJoinColumn(name = "transaction_id")
public class IncomeTransaction extends Transaction {

	@Column(length = 80)
	private String payer;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private IncomeStatus status;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "received_date")
	private LocalDate receivedDate;

	@AssertTrue(message = "RECEIVED incomes require receivedDate and account; PENDING incomes must not have them")
	public boolean isReceiptDataConsistent() {
		if (status == IncomeStatus.RECEIVED) {
			return receivedDate != null && account != null;
		}

		if (status == IncomeStatus.PENDING) {
			return receivedDate == null && account == null;
		}

		return true;
	}
}
