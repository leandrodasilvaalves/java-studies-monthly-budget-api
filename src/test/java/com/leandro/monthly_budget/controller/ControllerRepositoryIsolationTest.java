package com.leandro.monthly_budget.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.entities.CreditCardBill;
import com.leandro.monthly_budget.model.entities.CreditCardSubscription;
import com.leandro.monthly_budget.model.entities.FixedExpense;
import com.leandro.monthly_budget.model.entities.FixedIncome;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Project;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.services.AccountServiceImpl;
import com.leandro.monthly_budget.model.services.CategoryServiceImpl;
import com.leandro.monthly_budget.model.services.CreditCardBillServiceImpl;
import com.leandro.monthly_budget.model.services.CreditCardServiceImpl;
import com.leandro.monthly_budget.model.services.CreditCardSubscriptionServiceImpl;
import com.leandro.monthly_budget.model.services.FixedExpenseServiceImpl;
import com.leandro.monthly_budget.model.services.FixedIncomeServiceImpl;
import com.leandro.monthly_budget.model.services.PersonServiceImpl;
import com.leandro.monthly_budget.model.services.ProjectServiceImpl;
import com.leandro.monthly_budget.model.services.SubcategoryServiceImpl;
import com.leandro.monthly_budget.model.services.TagServiceImpl;
import com.leandro.monthly_budget.model.services.interfaces.AccountService;
import com.leandro.monthly_budget.model.services.interfaces.CategoryService;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardBillService;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardService;
import com.leandro.monthly_budget.model.services.interfaces.CreditCardSubscriptionService;
import com.leandro.monthly_budget.model.services.interfaces.FixedExpenseService;
import com.leandro.monthly_budget.model.services.interfaces.FixedIncomeService;
import com.leandro.monthly_budget.model.services.interfaces.PersonService;
import com.leandro.monthly_budget.model.services.interfaces.ProjectService;
import com.leandro.monthly_budget.model.services.interfaces.SubcategoryService;
import com.leandro.monthly_budget.model.services.interfaces.TagService;

class ControllerRepositoryIsolationTest {

	private static final List<Class<?>> CONTROLLERS = List.of(
			AccountsController.class,
			CategoriesController.class,
			CreditCardBillsController.class,
			CreditCardsController.class,
			CreditCardSubscriptionsController.class,
			FixedExpensesController.class,
			FixedIncomesController.class,
			PeopleController.class,
			ProjectsController.class,
			SubcategoriesController.class,
			TagsController.class);

	@Test
	void controllersDoNotDependOnRepositories() {
		for (Class<?> controller : CONTROLLERS) {
			for (Field field : controller.getDeclaredFields()) {
				assertThat(Repository.class.isAssignableFrom(field.getType()))
						.as("%s must not declare a repository field", controller.getSimpleName())
						.isFalse();
			}

			for (Constructor<?> constructor : controller.getDeclaredConstructors()) {
				for (Class<?> parameter : constructor.getParameterTypes()) {
					assertThat(Repository.class.isAssignableFrom(parameter))
							.as("%s must not receive a repository in its constructor",
									controller.getSimpleName())
							.isFalse();
				}
			}
		}
	}

	@Test
	void everyControllerBackedEntityHasItsOwnTransactionalService() {
		assertService(Account.class, AccountService.class, AccountServiceImpl.class);
		assertService(Category.class, CategoryService.class, CategoryServiceImpl.class);
		assertService(CreditCardBill.class, CreditCardBillService.class, CreditCardBillServiceImpl.class);
		assertService(CreditCard.class, CreditCardService.class, CreditCardServiceImpl.class);
		assertService(CreditCardSubscription.class, CreditCardSubscriptionService.class,
				CreditCardSubscriptionServiceImpl.class);
		assertService(FixedExpense.class, FixedExpenseService.class, FixedExpenseServiceImpl.class);
		assertService(FixedIncome.class, FixedIncomeService.class, FixedIncomeServiceImpl.class);
		assertService(Person.class, PersonService.class, PersonServiceImpl.class);
		assertService(Project.class, ProjectService.class, ProjectServiceImpl.class);
		assertService(Subcategory.class, SubcategoryService.class, SubcategoryServiceImpl.class);
		assertService(Tag.class, TagService.class, TagServiceImpl.class);
	}

	private static void assertService(
			Class<?> entity, Class<?> contract, Class<?> implementation) {

		assertThat(contract.isInterface())
				.as("%s must be an interface", contract.getSimpleName())
				.isTrue();
		assertThat(contract.isAssignableFrom(implementation))
				.as("%s must implement %s", implementation.getSimpleName(), contract.getSimpleName())
				.isTrue();
		assertThat(implementation.isAnnotationPresent(Service.class))
				.as("%s must be annotated with @Service", implementation.getSimpleName())
				.isTrue();
		assertThat(implementation.isAnnotationPresent(Transactional.class))
				.as("%s must be annotated with @Transactional", implementation.getSimpleName())
				.isTrue();
		assertThat(Arrays.stream(contract.getDeclaredMethods())
				.map(Method::getReturnType)
				.anyMatch(entity::equals))
				.as("%s must expose operations returning %s", contract.getSimpleName(), entity.getSimpleName())
				.isTrue();
	}
}
