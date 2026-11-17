package com.leandro.monthly_budget.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "credit_card_subscriptions")
public class CreditCardSubscriptions extends EntityBase {

	@ManyToOne
	@JoinColumn(name = "credit_card_id", nullable = false)
	private CreditCard creditCard;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "subcategory_id", nullable = false)
	private Subcategory subcategory;

	@Column(length = 80)
	private String store;

	@ManyToMany
	@JoinTable(
		name = "credit_card_subscription_people",
		joinColumns = @JoinColumn(name = "subscription_id"),
		inverseJoinColumns = @JoinColumn(name = "person_id")
	)
	private List<Person> people;

	@Column(nullable = false, length = 255)
	private String description;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	private boolean active = true;

	@ManyToMany
	@JoinTable(
		name = "credit_card_subscription_tags",
		joinColumns = @JoinColumn(name = "subscription_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private List<Tag> tags;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(name = "ignored", nullable = false)
	private boolean ignore = false;
}
