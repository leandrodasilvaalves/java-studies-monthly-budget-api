package com.leandro.monthly_budget.model.services.interfaces;

import org.springframework.data.domain.Page;

import com.leandro.monthly_budget.model.dtos.requests.TagRequest;
import com.leandro.monthly_budget.model.dtos.requests.enums.StatusRequest;
import com.leandro.monthly_budget.model.entities.Tag;

public interface TagService {

	Page<Tag> getAll(int page, int size, StatusRequest status);
	Tag getById(long id);
	Tag create(TagRequest request);
	Tag update(long id, TagRequest request);
	Tag activate(long id);
	Tag inactivate(long id);
}
