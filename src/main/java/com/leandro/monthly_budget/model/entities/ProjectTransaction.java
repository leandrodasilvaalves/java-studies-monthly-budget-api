package com.leandro.monthly_budget.model.entities;

import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ProjectTransactionStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionType;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project_transactions")
@DiscriminatorValue("PROJECT")
@PrimaryKeyJoinColumn(name = "transaction_id")
public class ProjectTransaction extends Transaction {

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "project_transaction_type", nullable = false, length = 20)
	private ProjectTransactionType projectTransactionType;

	@ManyToMany
	@JoinTable(
		name = "project_transaction_people",
		joinColumns = @JoinColumn(name = "transaction_id"),
		inverseJoinColumns = @JoinColumn(name = "person_id")
	)
	private List<Person> people;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProjectTransactionStatus status;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "movement_date")
	private LocalDate movementDate;

	@AssertTrue(message = "DONE project transactions require movementDate; PENDING ones require dueDate and no movementDate")
	public boolean isProjectDateConsistent() {
		if (status == ProjectTransactionStatus.DONE) {
			return movementDate != null;
		}

		if (status == ProjectTransactionStatus.PENDING) {
			return dueDate != null && movementDate == null;
		}

		return true;
	}

	@AssertTrue(message = "YIELD project transactions must not have an account; DEPOSIT and WITHDRAWAL require one")
	public boolean isProjectAccountConsistent() {
		if (projectTransactionType == ProjectTransactionType.YIELD) {
			return account == null;
		}

		if (projectTransactionType == ProjectTransactionType.DEPOSIT
			|| projectTransactionType == ProjectTransactionType.WITHDRAWAL) {
			return account != null;
		}

		return true;
	}
}
