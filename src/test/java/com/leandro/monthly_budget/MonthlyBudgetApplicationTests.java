package com.leandro.monthly_budget;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.SeedData;

@SpringBootTest
@ActiveProfiles("test")
class MonthlyBudgetApplicationTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SeedData seedData;

	@Test
	void contextLoads() {
	}

	@Test
	void seedDataIsLoaded() {
		assertThat(categoryRepository.count()).isPositive();
	}

	@Test
	void seedIsInvokedThroughTransactionalProxy() {
		assertThat(AopUtils.isAopProxy(seedData)).isTrue();
	}
}
