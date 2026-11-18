package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.AccountCreateRequest;
import com.leandro.monthly_budget.model.dtos.requests.AccountUpdateRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.AccountMapper;
import com.leandro.monthly_budget.model.repositories.AccountRepository;
import com.leandro.monthly_budget.model.services.interfaces.AccountService;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final AccountMapper mapper;

	public AccountServiceImpl(AccountRepository accountRepository, AccountMapper mapper) {
		this.accountRepository = accountRepository;
		this.mapper = mapper;
	}

	public Page<Account> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return status == StatusRequest.ALL
				? accountRepository.findAll(pageable)
				: accountRepository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
	}

	public Account getById(long id) {
		return accountRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
	}

	public Account create(AccountCreateRequest request) {

		if (accountRepository.existsByName(request.getName())) {
			throw new DuplicateResourceException("Already exists an account with this name");
		}

		return accountRepository.save(mapper.toEntity(request));
	}

	public Account update(long id, AccountUpdateRequest request) {

		var account = getById(id);
		account.setName(request.getName());

		return accountRepository.save(account);
	}

	public Account activate(long id) {

		var account = getById(id);
		account.setActive(true);

		return accountRepository.save(account);
	}

	public Account inactivate(long id) {

		var account = getById(id);
		account.setActive(false);

		return accountRepository.save(account);
	}
}
