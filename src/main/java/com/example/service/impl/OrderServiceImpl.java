package com.example.service.impl;

import com.example.dto.OrderDTO;
import com.example.dto.OrderItemDTO;
import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.mapper.DtoMapper;
import com.example.model.*;
import com.example.repository.OrderRepository;
import com.example.repository.PersonaRepository;
import com.example.repository.ProductRepository;
import com.example.repository.PromotionProductRepository;
import com.example.repository.StockAlertRepository;
import com.example.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import com.example.realtime.AppEventBroadcaster;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private static final int LOW_STOCK_THRESHOLD = 1;

	private final OrderRepository orderRepo;
	private final PersonaRepository personaRepo;
	private final ProductRepository productRepo;

	private final StockAlertRepository stockAlertRepo;
	private final PromotionProductRepository promoProductRepo;
	private final AppEventBroadcaster broadcaster;

	@Override
	public Page<OrderDTO> getAll(Pageable pageable) {
		return orderRepo.findAll(pageable).map(DtoMapper::toDto);
	}

	@Override
	public OrderDTO getById(Long idOrder) {
		Order order = orderRepo.findById(idOrder)
				.orElseThrow(() -> new NotFoundException("Ordine non trovato: " + idOrder));
		return DtoMapper.toDto(order);
	}

	@Override
	public Page<OrderDTO> getByPersona(Long idUser, Pageable pageable) {
		return orderRepo.findByUser_IdUser(idUser, pageable).map(DtoMapper::toDto);
	}

	@Override
	@Transactional
	public OrderDTO createForPersona(Long idUser, OrderDTO dto) {

		Persona pers = personaRepo.findById(idUser)
				.orElseThrow(() -> new NotFoundException("Persona non trovata: " + idUser));

		String description = dto.getDescription();
		if (description != null) {
			description = description.trim();
			if (description.isEmpty())
				description = null;
		}

		if (dto.getProducts() == null || dto.getProducts().isEmpty()) {
			throw new BadRequestException("Lista prodotti vuota");
		}

		Map<Long, Integer> qtyByProductId = new LinkedHashMap<>();
		for (OrderItemDTO item : dto.getProducts()) {
			if (item.getProductId() == null)
				throw new BadRequestException("productId mancante");
			if (item.getOrderedQuantity() <= 0)
				throw new BadRequestException("orderedQuantity non valido per productId=" + item.getProductId());
			qtyByProductId.merge(item.getProductId(), item.getOrderedQuantity(), Integer::sum);
		}

		Order order = new Order();
		order.setIdOrder(null);
		order.setDescription(description);
		order.setDateTime(LocalDateTime.now());
		order.setUser(pers);

		order.setShipFirstName(pers.getFirstName());
		order.setShipLastName(pers.getLastName());
		order.setShipPhone(pers.getPhone());
		order.setShipAddress(pers.getAddress());
		order.setShipCity(pers.getCity());

		List<OrderProduct> ops = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		for (Map.Entry<Long, Integer> entry : qtyByProductId.entrySet()) {
			Long productId = entry.getKey();
			int requestedQty = entry.getValue();

			Product product = productRepo.findById(productId)
					.orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + productId));

			int available = product.getQuantity();
			if (available < requestedQty) {
				throw new BadRequestException("Quantità insufficiente per productId=" + productId + " (disponibile="
						+ available + ", richiesta=" + requestedQty + ")");
			}

			int newQty = available - requestedQty;
			product.setQuantity(newQty);

			createLowStockAlertIfNeeded(product, newQty);

			double unitPrice = computeUnitPriceAtPurchase(product, now);

			OrderProduct op = new OrderProduct();
			op.setOrder(order);
			op.setProduct(product);
			op.setOrderedQuantity(requestedQty);
			op.setUnitPriceAtPurchase(unitPrice);
			ops.add(op);
		}

		order.setOrderProducts(ops);

		Order saved = orderRepo.save(order);

		broadcaster.publishInventoryChanged("ORDER_CREATED");

		return DtoMapper.toDto(saved);

	}

	@Override
	public void delete(Long idOrder) {
		if (!orderRepo.existsById(idOrder)) {
			throw new NotFoundException("Ordine non trovato: " + idOrder);
		}
		orderRepo.deleteById(idOrder);
	}

	@Override
	public Page<OrderDTO> searchAdmin(String customer, String product, String city, LocalDateTime from,
			LocalDateTime to, String period, Pageable pageable) {

		String c = normalize(customer);
		String p = normalize(product);
		String ct = normalize(city);

		if (from == null && to == null) {
			LocalDateTime[] range = computeRangeFromPeriod(period);
			if (range != null) {
				from = range[0];
				to = range[1];
			}
		}

		return orderRepo.adminSearch(c, p, ct, from, to, pageable).map(DtoMapper::toDto);
	}

	private LocalDateTime[] computeRangeFromPeriod(String period) {
		if (period == null)
			return null;

		String per = period.trim().toUpperCase();
		LocalDate today = LocalDate.now();

		return switch (per) {
		case "DAY" -> new LocalDateTime[] { today.atStartOfDay(), LocalDateTime.now() };
		case "WEEK" -> {
			LocalDate start = today.with(DayOfWeek.MONDAY);
			yield new LocalDateTime[] { start.atStartOfDay(), LocalDateTime.now() };
		}
		case "MONTH" -> {
			LocalDate start = today.withDayOfMonth(1);
			yield new LocalDateTime[] { start.atStartOfDay(), LocalDateTime.now() };
		}
		default -> null;
		};
	}

	private String normalize(String s) {
		if (s == null)
			return null;
		s = s.trim();
		return s.isEmpty() ? null : s;
	}

	private double computeUnitPriceAtPurchase(Product product, LocalDateTime now) {
		double base = product.getPrice();
		return promoProductRepo.findActiveForProductAt(product.getId(), now)
				.map(pp -> round2(base * (1.0 - pp.getDiscountPercent() / 100.0))).orElse(round2(base));
	}

	private double round2(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	private void createLowStockAlertIfNeeded(Product product, int newQty) {
		if (product == null)
			return;

		if (product.getCategory() == ProductCategory.USATO_RICONDIZIONATO)
			return;

		if (newQty > LOW_STOCK_THRESHOLD)
			return;

		var existingOpen = stockAlertRepo.findTop1ByProduct_IdAndStatusOrderByCreatedAtDesc(product.getId(),
				AlertStatus.OPEN);
		if (existingOpen.isPresent()) {
			StockAlert alert = existingOpen.get();
			alert.setCurrentQuantity(newQty);
			alert.setProductName(product.getName());
			alert.setThreshold(LOW_STOCK_THRESHOLD);
			stockAlertRepo.save(alert);
			return;
		}

		StockAlert alert = new StockAlert();
		alert.setCreatedAt(LocalDateTime.now());
		alert.setProduct(product);
		alert.setProductName(product.getName());
		alert.setCurrentQuantity(newQty);
		alert.setThreshold(LOW_STOCK_THRESHOLD);
		alert.setStatus(AlertStatus.OPEN);

		stockAlertRepo.save(alert);
	}

}
