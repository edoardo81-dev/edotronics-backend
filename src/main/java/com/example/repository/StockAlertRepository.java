package com.example.repository;

import com.example.model.AlertStatus;
import com.example.model.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {

    boolean existsByProduct_IdAndStatus(Long productId, AlertStatus status);

    List<StockAlert> findByStatusOrderByCreatedAtDesc(AlertStatus status);

    Optional<StockAlert> findTop1ByProduct_IdAndStatusOrderByCreatedAtDesc(Long productId, AlertStatus status);

    long countByStatus(AlertStatus status);
}
