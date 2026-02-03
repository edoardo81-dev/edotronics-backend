package com.example.controller;

import com.example.dto.OrderDTO;
import com.example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

	private final OrderService orderService;

	@GetMapping
	public Page<OrderDTO> getAll(@PageableDefault(size = 10, sort = "dateTime") Pageable pageable) {
		return orderService.getAll(pageable);
	}

	@GetMapping("/{idOrder}")
	public OrderDTO getById(@PathVariable Long idOrder) {
		return orderService.getById(idOrder);
	}

	@DeleteMapping("/{idOrder}")
	public void delete(@PathVariable Long idOrder) {
		orderService.delete(idOrder);
	}

	@GetMapping("/search")
	public Page<OrderDTO> search(@RequestParam(required = false) String customer,
			@RequestParam(required = false) String product, @RequestParam(required = false) String city,
			@RequestParam(required = false) LocalDateTime from, @RequestParam(required = false) LocalDateTime to,
			@RequestParam(required = false) String period, // DAY | WEEK | MONTH
			@PageableDefault(size = 10, sort = "dateTime") Pageable pageable) {
		return orderService.searchAdmin(customer, product, city, from, to, period, pageable);
	}
}
