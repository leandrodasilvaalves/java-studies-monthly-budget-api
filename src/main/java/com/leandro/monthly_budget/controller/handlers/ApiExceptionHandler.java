package com.leandro.monthly_budget.controller.handlers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.leandro.monthly_budget.model.exceptions.BusinessRuleException;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;

/**
 * Contrato de erro da API (RFC 9457 / {@link ProblemDetail}).
 *
 * <p>Ao herdar de {@link ResponseEntityExceptionHandler}, os erros padrao do
 * Spring MVC (405, 415, 404, parametros ausentes, JSON malformado, tipo invalido,
 * {@code ResponseStatusException}, etc.) ja sao tratados automaticamente.
 *
 * <p>Aqui ficam apenas os casos em que o formato precisa ser customizado:
 * a lista estruturada de erros por campo e o conflito de banco de dados.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // ---------- @Valid on request body (@RequestBody) ----------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        var pd = problem(status, "Validation failed",
                "One or more request body fields are invalid.");
        pd.setProperty("errors", fieldErrors(ex.getBindingResult().getFieldErrors()));
        return handleExceptionInternal(ex, pd, headers, status, request);
    }

    // ---------- constraints on controller parameters (@RequestParam, @PathVariable, ...) ----------

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        var pd = problem(ex.getStatusCode(), "Validation failed",
                ex.isForReturnValue()
                        ? "The server response violated a constraint."
                        : "One or more request parameters are invalid.");
        pd.setProperty("errors", validationErrors(ex));
        return handleExceptionInternal(ex, pd, headers, status, request);
    }

    // ---------- data conflict (unique, FK, ...) ----------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail onDataIntegrity(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "Data conflict",
                "The operation violates a database constraint.");
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail onNotFound(ResourceNotFoundException ex) {
    	return problem(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }
    
    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail onBusinessRuleException(BusinessRuleException ex) {
    	return problem(HttpStatus.BAD_REQUEST, "Business Rule Error", ex.getMessage());
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    ProblemDetail onDuplicateName(DuplicateResourceException ex) { 
    	return problem(HttpStatus.CONFLICT, "Duplicate Resource Error", ex.getMessage());
    } 

    // ---------- helpers ----------

    private static ProblemDetail problem(HttpStatusCode status, String title, String detail) {
        var pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        return pd;
    }

    private static Map<String, Object> error(String field, String message, Object rejectedValue) {
        var map = new LinkedHashMap<String, Object>();   // accepts null, unlike Map.of
        map.put("field", field);
        map.put("message", message);
        if (rejectedValue != null) {
            map.put("rejectedValue", rejectedValue);
        }
        return map;
    }

    private static String prefix(String prefix, String field) {
        return (prefix == null || field == null) ? field : prefix + "." + field;
    }

    private List<Map<String, Object>> fieldErrors(List<FieldError> errors) {
        return errors.stream()
                .map(fe -> error(fe.getField(), resolve(fe), fe.getRejectedValue()))
                .toList();
    }

    private List<Map<String, Object>> validationErrors(MethodValidationResult result) {
        var errors = new ArrayList<Map<String, Object>>();

        for (ParameterValidationResult pvr : result.getParameterValidationResults()) {
            String parameter = pvr.getMethodParameter().getParameterName();

            if (pvr instanceof ParameterErrors beanErrors) {
                for (FieldError fe : beanErrors.getFieldErrors()) {
                    errors.add(error(prefix(parameter, fe.getField()),
                            resolve(fe), fe.getRejectedValue()));
                }
                for (ObjectError ge : beanErrors.getGlobalErrors()) {
                    errors.add(error(parameter, resolve(ge), null));
                }
            }
            else {
                for (MessageSourceResolvable err : pvr.getResolvableErrors()) {
                    errors.add(error(parameter, resolve(err), pvr.getArgument()));
                }
            }
        }

        for (MessageSourceResolvable err : result.getCrossParameterValidationResults()) {
            errors.add(error("_cross", resolve(err), null));
        }

        return errors;
    }

    private String resolve(MessageSourceResolvable error) {
        try {
            return messageSource.getMessage(error, LocaleContextHolder.getLocale());
        }
        catch (NoSuchMessageException ex) {
            return error.getDefaultMessage();
        }
    }
}
