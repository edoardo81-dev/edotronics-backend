package com.example.repository;

import com.example.model.Promotion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

	@Override
	@EntityGraph(attributePaths = { "items", "items.product" })
	List<Promotion> findAll();

	@Override
	@EntityGraph(attributePaths = { "items", "items.product" })
	Optional<Promotion> findById(Long id);

	@EntityGraph(attributePaths = { "items", "items.product" })
	List<Promotion> findByArchivedFalse();

	@EntityGraph(attributePaths = { "items", "items.product" })
	List<Promotion> findAllByOrderByIdDesc();
}
