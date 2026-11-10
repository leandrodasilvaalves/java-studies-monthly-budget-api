package com.leandro.monthly_budget;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.leandro.monthly_budget.model.repositories.CategoryRepository;
import com.leandro.monthly_budget.model.repositories.SeedData;
import com.leandro.monthly_budget.model.repositories.TagRepository;

@SpringBootApplication
@EnableJpaAuditing
public class MonthlyBudgetApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonthlyBudgetApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(
			CategoryRepository categoryRepository,
			TagRepository tagRepository) {
		return args ->{
			SeedData.init(categoryRepository,tagRepository);
		};
	}
}
