package com.leandro.monthly_budget.model.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.dtos.requests.TagRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.exceptions.DuplicateResourceException;
import com.leandro.monthly_budget.model.exceptions.ResourceNotFoundException;
import com.leandro.monthly_budget.model.mappers.TagMapper;
import com.leandro.monthly_budget.model.repositories.TagRepository;
import com.leandro.monthly_budget.model.services.interfaces.TagService;

@Service
@Transactional
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;
	private final TagMapper mapper;

	public TagServiceImpl(TagRepository tagRepository, TagMapper mapper) {
		this.tagRepository = tagRepository;
		this.mapper = mapper;
	}

	public Page<Tag> getAll(int page, int size, StatusRequest status) {

		Pageable pageable = PageRequest.of(page - 1, size);

		return status == StatusRequest.ALL
				? tagRepository.findAll(pageable)
				: tagRepository.findAllByStatus(status == StatusRequest.ACTIVE, pageable);
	}

	public Tag getById(long id) {
		return tagRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
	}

	public Tag create(TagRequest request) {

		if (tagRepository.existsByName(request.getName())) {
			throw new DuplicateResourceException("Already exists a tag with this name");
		}

		return tagRepository.save(mapper.toEntity(request));
	}

	public Tag update(long id, TagRequest request) {

		var tag = getById(id);
		tag.setName(request.getName());

		return tagRepository.save(tag);
	}

	public Tag activate(long id) {

		var tag = getById(id);
		tag.setActive(true);

		return tagRepository.save(tag);
	}

	public Tag inactivate(long id) {

		var tag = getById(id);
		tag.setActive(false);

		return tagRepository.save(tag);
	}
}
