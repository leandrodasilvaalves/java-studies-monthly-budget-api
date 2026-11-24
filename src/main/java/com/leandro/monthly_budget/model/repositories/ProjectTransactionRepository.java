package com.leandro.monthly_budget.model.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.leandro.monthly_budget.model.entities.ProjectTransaction;

public interface ProjectTransactionRepository extends JpaRepository<ProjectTransaction, Long> {

	@Query("SELECT t FROM ProjectTransaction t WHERE t.project.id = :projectId")
	Page<ProjectTransaction> findAllByProjectId(long projectId, Pageable pageable);

	@Query("SELECT t FROM ProjectTransaction t WHERE t.project.id = :projectId AND t.active = :active")
	Page<ProjectTransaction> findAllByProjectIdAndStatus(long projectId, boolean active, Pageable pageable);

	@Query("SELECT t FROM ProjectTransaction t WHERE t.id = :id AND t.project.id = :projectId")
	Optional<ProjectTransaction> findByIdAndProjectId(long id, long projectId);
}
