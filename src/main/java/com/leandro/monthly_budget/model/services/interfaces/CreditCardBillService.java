package com.leandro.monthly_budget.model.services.interfaces;

import java.time.YearMonth;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.entities.CreditCardBill;
import com.leandro.monthly_budget.model.entities.CreditCardSubscription;

/**
 * Contrato da fatura do cartao de credito.
 *
 * <p>A fatura nao existe por si so: ela e derivada das transacoes e assinaturas
 * ativas do cartao. Por isso nao ha operacoes de criacao/atualizacao/remocao
 * expostas na API; a geracao acontece como efeito de transacoes e assinaturas.
 */
public interface CreditCardBillService {

	Page<CreditCardBill> getAll(long creditCardId, int page, int size);

	CreditCardBill getById(long creditCardId, long id);

	/**
	 * Garante a existencia da fatura do cartao para o periodo informado,
	 * vinculando as assinaturas ativas daquele mes.
	 */
	CreditCardBill requireBill(long creditCardId, YearMonth period);

	/**
	 * Sincroniza as faturas existentes (e cria as que faltam quando a assinatura
	 * esta ativa) para todos os meses cobertos pela assinatura.
	 */
	void syncBillsForSubscription(CreditCardSubscription subscription);
}
