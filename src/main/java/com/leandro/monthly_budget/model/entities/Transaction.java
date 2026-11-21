package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tx_kind", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Transaction extends EntityBase {

	@NotNull
	@Column(name = "registered_date", nullable = false)
	private LocalDate registeredDate;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "subcategory_id", nullable = false)
	private Subcategory subcategory;

	@ManyToMany
	@JoinTable(
		name = "transaction_tags",
		joinColumns = @JoinColumn(name = "transaction_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private List<Tag> tags;

	@Column(nullable = false, length = 255)
	private String description;

	@NotNull
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "ignored", nullable = false)
	private boolean ignore = false;

	@Column(length = 1000)
	private String notes;
}
