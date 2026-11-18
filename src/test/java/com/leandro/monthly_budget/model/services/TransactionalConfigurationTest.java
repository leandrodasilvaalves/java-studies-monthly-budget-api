package com.leandro.monthly_budget.model.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.repositories.SeedData;

class TransactionalConfigurationTest {

	@Test
	void allWriteServicesAreTransactional() {
		assertThat(FixedIncomeServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(FixedExpenseServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(ProjectServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(CreditCardSubscriptionServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(AccountServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(CategoryServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(CreditCardServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(PersonServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(SubcategoryServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
		assertThat(TagServiceImpl.class.isAnnotationPresent(Transactional.class)).isTrue();
	}

	@Test
	void seedIsTransactional() throws NoSuchMethodException {
		Method init = SeedData.class.getDeclaredMethod("init");

		assertThat(init.isAnnotationPresent(Transactional.class)).isTrue();
	}
}
