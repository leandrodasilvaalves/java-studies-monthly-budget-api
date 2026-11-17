package com.leandro.monthly_budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class ControllerValidationTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void blankAccountNameIsRejected() throws Exception {
		postWithInvalidName("/api/v1/accounts", "{\"name\":\"\",\"balance\":100}");
	}

	@Test
	void blankTagNameIsRejected() throws Exception {
		postWithInvalidName("/api/v1/tags", "{\"name\":\"\"}");
	}

	@Test
	void blankPersonNameIsRejected() throws Exception {
		postWithInvalidName("/api/v1/people", "{\"name\":\"\"}");
	}

	@Test
	void blankCreditCardNameIsRejected() throws Exception {
		postWithInvalidName("/api/v1/creditcards", "{\"name\":\"\",\"dueDay\":10}");
	}

	@Test
	void blankSubcategoryNameIsRejected() throws Exception {
		postWithInvalidName("/api/v1/categories/999/subcategories", "{\"name\":\"\"}");
	}

	@Test
	void invalidPageIsRejectedByNativeMethodValidation() throws Exception {
		mockMvc.perform(get("/api/v1/accounts").param("page", "0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Validation failed"))
				.andExpect(jsonPath("$.errors[?(@.field == 'page')]").exists());
	}

	@Test
	void invalidPathVariableIsRejected() throws Exception {
		mockMvc.perform(get("/api/v1/tags/0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Validation failed"));
	}

	private void postWithInvalidName(String url, String body) throws Exception {
		mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Validation failed"))
				.andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists());
	}
}
