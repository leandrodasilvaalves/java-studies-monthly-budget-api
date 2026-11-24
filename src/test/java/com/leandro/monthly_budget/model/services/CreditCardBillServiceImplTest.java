package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.entities.CreditCardBill;
import com.leandro.monthly_budget.model.entities.CreditCardSubscription;
import com.leandro.monthly_budget.model.enums.CreditCardBillStatus;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.repositories.CreditCardBillRepository;
import com.leandro.monthly_budget.model.repositories.CreditCardSubscriptionRepository;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@ExtendWith(MockitoExtension.class)
class CreditCardBillServiceImplTest {

	@Mock private CreditCardBillRepository repository;
	@Mock private CreditCardSubscriptionRepository subscriptionRepository;
	@Mock private ReferenceDataValidator validator;
	@InjectMocks private CreditCardBillServiceImpl service;

	@Test
	void getAllValidatesCreditCardAndListsBills() {
		var pageable = PageRequest.of(0, 20);
		var bill = new CreditCardBill();
		when(repository.findAllByCreditCardId(7L, pageable))
				.thenReturn(new PageImpl<>(List.of(bill)));

		var result = service.getAll(7L, 1, 20);

		assertThat(result.getContent()).containsExactly(bill);
		verify(validator).requireCreditCard(7L);
	}

	@Test
	void getAllUsesOneBasedPage() {
		var pageable = PageRequest.of(1, 10);
		when(repository.findAllByCreditCardId(7L, pageable))
				.thenReturn(new PageImpl<>(List.of()));

		service.getAll(7L, 2, 10);

		verify(repository).findAllByCreditCardId(7L, pageable);
	}

	@Test
	void getByIdThrowsWhenBillDoesNotBelongToCreditCard() {
		when(repository.findByIdAndCreditCardId(99L, 7L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(7L, 99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Credit card bill not found");
	}

	@Test
	void getByIdReturnsTheBill() {
		var bill = new CreditCardBill();
		when(repository.findByIdAndCreditCardId(5L, 7L)).thenReturn(Optional.of(bill));

		assertThat(service.getById(7L, 5L)).isSameAs(bill);
	}

	@Test
	void requireBillCreatesBillWhenMissing() {
		var card = card(7L, 10);
		var start = LocalDate.of(2026, 11, 1);
		var end = LocalDate.of(2026, 11, 30);

		when(validator.requireCreditCard(7L)).thenReturn(card);
		when(repository.findByCreditCardIdAndDueDateBetween(7L, start, end))
				.thenReturn(Optional.empty());
		when(subscriptionRepository.findActiveByCreditCardIdAndPeriod(7L, start, end))
				.thenReturn(List.of());
		when(repository.save(any(CreditCardBill.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		var bill = service.requireBill(7L, YearMonth.of(2026, 11));

		assertThat(bill.getCreditcard()).isSameAs(card);
		assertThat(bill.getDueDate()).isEqualTo(LocalDate.of(2026, 11, 10));
		assertThat(bill.getStatus()).isEqualTo(CreditCardBillStatus.PENDING);
	}

	@Test
	void requireBillClampsDueDayToMonthLength() {
		var card = card(7L, 31);
		var start = LocalDate.of(2026, 2, 1);
		var end = LocalDate.of(2026, 2, 28);

		when(validator.requireCreditCard(7L)).thenReturn(card);
		when(repository.findByCreditCardIdAndDueDateBetween(7L, start, end))
				.thenReturn(Optional.empty());
		when(subscriptionRepository.findActiveByCreditCardIdAndPeriod(7L, start, end))
				.thenReturn(List.of());
		when(repository.save(any(CreditCardBill.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		var bill = service.requireBill(7L, YearMonth.of(2026, 2));

		assertThat(bill.getDueDate()).isEqualTo(LocalDate.of(2026, 2, 28));
	}

	@Test
	void requireBillLinksActiveSubscriptions() {
		var card = card(7L, 10);
		var bill = new CreditCardBill();
		bill.setCreditcard(card);
		var subscription = new CreditCardSubscription();
		var start = LocalDate.of(2026, 2, 1);
		var end = LocalDate.of(2026, 2, 28);

		when(validator.requireCreditCard(7L)).thenReturn(card);
		when(repository.findByCreditCardIdAndDueDateBetween(7L, start, end))
				.thenReturn(Optional.of(bill));
		when(subscriptionRepository.findActiveByCreditCardIdAndPeriod(7L, start, end))
				.thenReturn(List.of(subscription));
		when(repository.save(bill)).thenReturn(bill);

		var result = service.requireBill(7L, YearMonth.of(2026, 2));

		assertThat(result.getSubscriptions()).containsExactly(subscription);
	}

	@Test
	void syncBillsForSubscriptionCreatesBillsForActiveSubscription() {
		var card = card(7L, 10);
		var subscription = subscription(card, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31), true);

		when(validator.requireCreditCard(7L)).thenReturn(card);
		when(repository.findByCreditCardIdAndDueDateBetween(anyLong(), any(), any()))
				.thenReturn(Optional.empty());
		when(subscriptionRepository.findActiveByCreditCardIdAndPeriod(anyLong(), any(), any()))
				.thenReturn(List.of());
		when(repository.save(any(CreditCardBill.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		service.syncBillsForSubscription(subscription);

		verify(repository, times(3)).save(any(CreditCardBill.class));
	}

	@Test
	void syncBillsForSubscriptionDoesNotCreateBillsWhenInactive() {
		var card = card(7L, 10);
		var subscription = subscription(card, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31), false);

		when(repository.findByCreditCardIdAndDueDateBetween(anyLong(), any(), any()))
				.thenReturn(Optional.empty());

		service.syncBillsForSubscription(subscription);

		verify(repository, never()).save(any());
	}

	private static CreditCard card(long id, int dueDay) {
		var card = new CreditCard();
		card.setId(id);
		card.setDueDay(dueDay);

		return card;
	}

	private static CreditCardSubscription subscription(
			CreditCard card, LocalDate startDate, LocalDate endDate, boolean active) {
		var subscription = new CreditCardSubscription();
		subscription.setCreditCard(card);
		subscription.setStartDate(startDate);
		subscription.setEndDate(endDate);
		subscription.setActive(active);

		return subscription;
	}
}
