package com.leandro.monthly_budget.controller.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.leandro.monthly_budget.model.dtos.requests.CategoryRequest;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.mappers.CategoryMapper;

class CategoryMapperTest {

	private final CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

	@Test
	void mapsRequestToEntity() {
		var request = new CategoryRequest("Food", CategoryType.Expense);

		var category = mapper.toEntity(request);

		assertThat(category.getName()).isEqualTo("Food");
		assertThat(category.getType()).isEqualTo(CategoryType.Expense);
	}

	@Test
	void mapsEntityToResponse() {
		var category = new Category();
		category.setId(1L);
		category.setName("Salary");
		category.setType(CategoryType.Income);

		var response = mapper.toResponse(category);

		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("Salary");
		assertThat(response.getType()).isEqualTo(CategoryType.Income);
	}

	@Test
	void mapsNullToNull() {
		assertThat(mapper.toEntity(null)).isNull();
		assertThat(mapper.toResponse(null)).isNull();
	}
}
