package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.PersonRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.PersonMapper;
import com.leandro.monthly_budget.model.repositories.PersonRepository;
import com.leandro.monthly_budget.model.services.interfaces.PersonService;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

	private final PersonRepository personRepository;
	private final PersonMapper mapper;

	public PersonServiceImpl(PersonRepository personRepository, PersonMapper mapper) {
		this.personRepository = personRepository;
		this.mapper = mapper;
	}

	public Page<Person> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return status == StatusRequest.ALL
				? personRepository.findAll(pageable)
				: personRepository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
	}

	public Person getById(long id) {
		return personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Person not found"));
	}

	public Person create(PersonRequest request) {

		if (personRepository.existsByName(request.getName())) {
			throw new DuplicateResourceException("Already exists a person with this name");
		}

		return personRepository.save(mapper.toEntity(request));
	}

	public Person update(long id, PersonRequest request) {

		var person = getById(id);
		person.setName(request.getName());

		return personRepository.save(person);
	}

	public Person activate(long id) {

		var person = getById(id);
		person.setActive(true);

		return personRepository.save(person);
	}

	public Person inactivate(long id) {

		var person = getById(id);
		person.setActive(false);

		return personRepository.save(person);
	}
}
