package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.leandro.monthly_budget.model.entities.CreditCardBill;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.repositories.CreditCardBillRepository;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

@ExtendWith(MockitoExtension.class)
class CreditCardBillServiceImplTest {

	@Mock private CreditCardBillRepository repository;
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
}
