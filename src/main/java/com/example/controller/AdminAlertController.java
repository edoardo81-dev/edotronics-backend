package com.example.controller;

import com.example.dto.StockAlertDTO;
import com.example.exception.NotFoundException;
import com.example.mapper.DtoMapper;
import com.example.model.AlertStatus;
import com.example.model.StockAlert;
import com.example.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/alerts")
@RequiredArgsConstructor
public class AdminAlertController {

	private final StockAlertRepository stockAlertRepo;

	@GetMapping
	public List<StockAlertDTO> getOpenAlerts() {
		List<StockAlert> alerts = stockAlertRepo.findByStatusOrderByCreatedAtDesc(AlertStatus.OPEN);
		return DtoMapper.toStockAlertDtoList(alerts);
	}

	@GetMapping("/open/count")
	public long countOpenAlerts() {
		return stockAlertRepo.countByStatus(AlertStatus.OPEN);
	}

	@PostMapping("/{id}/ack")
	public StockAlertDTO ack(@PathVariable Long id) {
		StockAlert alert = stockAlertRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("Alert non trovato: " + id));

		alert.setStatus(AlertStatus.ACK);
		StockAlert saved = stockAlertRepo.save(alert);

		return DtoMapper.toDto(saved);
	}
}
