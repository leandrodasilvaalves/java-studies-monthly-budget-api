package com.leandro.monthly_budget.controller.handlers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

class ApiExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var handler = new ApiExceptionHandler(new StaticMessageSource());
        this.mockMvc = MockMvcBuilders.standaloneSetup(new StubController())
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void invalidBodyReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/stub/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void invalidParamReturnsFieldErrors() throws Exception {
        mockMvc.perform(get("/stub/param").param("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("page"));
    }

    @Test
    void invalidParamTypeIsHandledByParent() throws Exception {
        mockMvc.perform(get("/stub/param").param("page", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("page")));
    }

    @Test
    void malformedJsonIsHandledByParent() throws Exception {
        mockMvc.perform(post("/stub/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to read request"));
    }

    @Test
    void dataIntegrityReturnsConflict() throws Exception {
        mockMvc.perform(get("/stub/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Data conflict"));
    }

    @Test
    void responseStatusIsHandledByParentAsProblemDetail() throws Exception {
        mockMvc.perform(get("/stub/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("missing"));
    }

    @RestController
    static class StubController {

        @PostMapping("/stub/body")
        void body(@Valid @RequestBody Payload payload) {
        }

        @GetMapping("/stub/param")
        void param(@RequestParam @Min(1) int page) {
        }

        @GetMapping("/stub/conflict")
        void conflict() {
            throw new DataIntegrityViolationException("boom");
        }

        @GetMapping("/stub/not-found")
        void notFound() {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "missing");
        }
    }

    record Payload(@NotBlank String name) {
    }
}
