package com.leandro.monthly_budget.model.services;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.entities.CreditCardBill;
import com.leandro.monthly_budget.model.entities.CreditCardSubscription;
import com.leandro.monthly_budget.model.enums.CreditCardBillStatus;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.repositories.CreditCardBillRepository;
import com.leandro.monthly_budget.model.repositories.CreditCardSubscriptionRepository;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardBillService;
import com.leandro.monthly_budget.model.services.support.ReferenceDataValidator;

/**
 * Servico da fatura.
 *
 * <p>As operacoes de leitura sao read-only. A geracao/atualizacao da fatura
 * acontece como efeito de transacoes e assinaturas ({@link #requireBill} e
 * {@link #syncBillsForSubscription}). O total e calculado em
 * {@link CreditCardBill#getAmount()} e volta zerado quando a fatura nao possui
 * lancamentos.
 */
@Service
@Transactional(readOnly = true)
public class CreditCardBillServiceImpl implements CreditCardBillService {

	private final CreditCardBillRepository billRepository;
	private final CreditCardSubscriptionRepository subscriptionRepository;
	private final ReferenceDataValidator validator;

	public CreditCardBillServiceImpl(
			CreditCardBillRepository billRepository,
			CreditCardSubscriptionRepository subscriptionRepository,
			ReferenceDataValidator validator) {
		this.billRepository = billRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.validator = validator;
	}

	public Page<CreditCardBill> getAll(long creditCardId, int page, int size) {

		validator.requireCreditCard(creditCardId);

		Pageable pageable = PageRequest.of(page - 1, size);

		return billRepository.findAllByCreditCardId(creditCardId, pageable);
	}

	public CreditCardBill getById(long creditCardId, long id) {
		return billRepository.findByIdAndCreditCardId(id, creditCardId)
				.orElseThrow(() -> new ResourceNotFoundException("Credit card bill not found"));
	}

	@Transactional
	public CreditCardBill requireBill(long creditCardId, YearMonth period) {

		var creditCard = validator.requireCreditCard(creditCardId);
		var start = period.atDay(1);
		var end = period.atEndOfMonth();

		var bill = billRepository.findByCreditCardIdAndDueDateBetween(creditCardId, start, end)
				.orElseGet(() -> newBill(creditCard, period));

		bill.setSubscriptions(
				subscriptionRepository.findActiveByCreditCardIdAndPeriod(creditCardId, start, end));

		return billRepository.save(bill);
	}

	@Transactional
	public void syncBillsForSubscription(CreditCardSubscription subscription) {

		var creditCardId = subscription.getCreditCard().getId();
		var start = YearMonth.from(subscription.getStartDate());
		var end = YearMonth.from(subscription.getEndDate());
		var shouldExist = subscription.isActive() && !subscription.isIgnore();

		for (YearMonth period = start; !period.isAfter(end); period = period.plusMonths(1)) {
			boolean exists = billRepository
					.findByCreditCardIdAndDueDateBetween(creditCardId, period.atDay(1), period.atEndOfMonth())
					.isPresent();

			if (exists || shouldExist) {
				requireBill(creditCardId, period);
			}
		}
	}

	private static CreditCardBill newBill(CreditCard creditCard, YearMonth period) {

		var bill = new CreditCardBill();
		bill.setCreditcard(creditCard);
		bill.setDueDate(dueDateFor(creditCard, period));
		bill.setStatus(CreditCardBillStatus.PENDING);

		return bill;
	}

	private static LocalDate dueDateFor(CreditCard creditCard, YearMonth period) {
		int day = Math.max(1, Math.min(creditCard.getDueDay(), period.lengthOfMonth()));

		return period.atDay(day);
	}
}
