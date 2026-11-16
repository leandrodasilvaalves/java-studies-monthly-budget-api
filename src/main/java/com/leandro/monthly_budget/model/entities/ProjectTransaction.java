package com.leandro.monthly_budget.model.entities;

import java.time.LocalDate;
import java.util.List;

import com.leandro.monthly_budget.model.enums.ProjectTransactionStatus;
import com.leandro.monthly_budget.model.enums.ProjectTransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTransaction extends Transaction {
	private Project project;
	private LocalDate dueDate;
	private ProjectTransactionType projectTransactionType;
	private List<Person> people;
	private ProjectTransactionStatus status;
}