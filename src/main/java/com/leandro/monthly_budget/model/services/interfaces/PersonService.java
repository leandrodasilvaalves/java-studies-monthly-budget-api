package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.PersonRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Person;

public interface PersonService {

	Page<Person> getAll(int page, int size, StatusRequest status);
	Person getById(long id);
	Person create(PersonRequest request);
	Person update(long id, PersonRequest request);
	Person activate(long id);
	Person inactivate(long id);
}
