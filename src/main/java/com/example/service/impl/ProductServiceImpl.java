package com.example.service.impl;

import com.example.dto.CreateProductRequestDTO;
import com.example.dto.ProductDTO;
import com.example.dto.UpdateProductRequestDTO;
import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.mapper.DtoMapper;
import com.example.model.AlertStatus;
import com.example.model.Product;
import com.example.model.ProductCategory;
import com.example.model.StockAlert;
import com.example.realtime.AppEventBroadcaster;
import com.example.repository.ProductRepository;
import com.example.repository.ProductSalesView;
import com.example.repository.PromotionProductRepository;
import com.example.repository.StockAlertRepository;
import com.example.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private static final int LOW_STOCK_THRESHOLD = 1;

	private final ProductRepository productRepository;
	private final StockAlertRepository stockAlertRepo;
	private final PromotionProductRepository promoProductRepo;
	private final AppEventBroadcaster broadcaster;

	@Override
	public Page<ProductDTO> getAll(String q, ProductCategory category, Pageable pageable) {
		LocalDateTime now = LocalDateTime.now();
		String query = normalize(q);

		Page<Product> page;

		if (category != null && query != null) {
			page = productRepository.findByCategoryAndNameContainingIgnoreCase(category, query, pageable);
		} else if (category != null) {
			page = productRepository.findByCategory(category, pageable);
		} else if (query != null) {
			page = productRepository.findByNameContainingIgnoreCase(query, pageable);
		} else {
			page = productRepository.findAll(pageable);
		}

		return page.map(p -> applyPromotion(DtoMapper.toProductDto(p), p.getId(), now));
	}

	@Override
	public ProductDTO getById(Long id) {
		Product p = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + id));

		LocalDateTime now = LocalDateTime.now();
		return applyPromotion(DtoMapper.toProductDto(p), p.getId(), now);
	}

	@Override
	public ProductDTO create(CreateProductRequestDTO dto) {
		if (dto == null)
			throw new BadRequestException("Body mancante");

		if (dto.getName() == null || dto.getName().trim().isEmpty()) {
			throw new BadRequestException("Nome obbligatorio.");
		}
		if (dto.getPrice() == null || dto.getPrice() < 0) {
			throw new BadRequestException("Prezzo non valido (deve essere >= 0).");
		}
		if (dto.getQuantity() < 0) {
			throw new BadRequestException("Quantità non valida (deve essere >= 0).");
		}
		if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty()) {
			throw new BadRequestException("Image URL obbligatoria.");
		}
		if (dto.getCategory() == null) {
			throw new BadRequestException("Categoria obbligatoria.");
		}

		Product entity = new Product();
		entity.setId(null);
		entity.setName(dto.getName().trim());
		entity.setPrice(dto.getPrice());
		entity.setQuantity(dto.getQuantity());
		entity.setImageUrl(dto.getImageUrl().trim());
		entity.setCategory(dto.getCategory());

		Product saved = productRepository.save(entity);

		syncStockAlert(saved);

		broadcaster.publishInventoryChanged("PRODUCT_CREATED");

		return DtoMapper.toProductDto(saved);
	}

	@Override
	public ProductDTO update(Long id, UpdateProductRequestDTO dto) {
		Product p = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + id));

		if (dto == null)
			throw new BadRequestException("Body mancante");
		if (dto.getPrice() == null || dto.getPrice() <= 0) {
			throw new BadRequestException("Prezzo non valido (deve essere > 0).");
		}
		if (dto.getName() == null || dto.getName().trim().isEmpty()) {
			throw new BadRequestException("Nome obbligatorio.");
		}
		if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty()) {
			throw new BadRequestException("Image URL obbligatoria.");
		}
		if (dto.getCategory() == null) {
			throw new BadRequestException("Categoria obbligatoria.");
		}

		p.setName(dto.getName().trim());
		p.setPrice(dto.getPrice());
		p.setImageUrl(dto.getImageUrl().trim());
		p.setCategory(dto.getCategory());

		syncStockAlert(p);

		Product saved = productRepository.save(p);

		broadcaster.publishInventoryChanged("PRODUCT_UPDATED");

		LocalDateTime now = LocalDateTime.now();
		return applyPromotion(DtoMapper.toProductDto(saved), saved.getId(), now);
	}

	@Override
	public void delete(Long id) {
		if (!productRepository.existsById(id)) {
			throw new NotFoundException("Prodotto non trovato: " + id);
		}

		productRepository.deleteById(id);

		broadcaster.publishInventoryChanged("PRODUCT_DELETED");
	}

	@Override
	public List<ProductCategory> getCategories() {
		return Arrays.asList(ProductCategory.values());
	}

	@Override
	public List<ProductSalesView> topSelling(int limit) {
		int n = Math.max(1, limit);
		return productRepository.topSelling(PageRequest.of(0, n));
	}

	@Override
	public List<ProductSalesView> leastSelling(int limit) {
		int n = Math.max(1, limit);
		return productRepository.leastSelling(PageRequest.of(0, n));
	}

	@Override
	public List<ProductSalesView> topSelling(int limit, Integer days) {
		int n = Math.max(1, limit);

		if (days == null)
			return productRepository.topSelling(PageRequest.of(0, n));
		if (days < 1)
			throw new BadRequestException("days deve essere >= 1");

		LocalDateTime from = LocalDateTime.now().minusDays(days);
		return productRepository.topSellingSince(from, PageRequest.of(0, n));
	}

	@Override
	public List<ProductSalesView> leastSelling(int limit, Integer days) {
		int n = Math.max(1, limit);

		if (days == null)
			return productRepository.leastSelling(PageRequest.of(0, n));
		if (days < 1)
			throw new BadRequestException("days deve essere >= 1");

		LocalDateTime from = LocalDateTime.now().minusDays(days);
		return productRepository.leastSellingSince(from, PageRequest.of(0, n));
	}

	private String normalize(String s) {
		if (s == null)
			return null;
		s = s.trim();
		return s.isEmpty() ? null : s;
	}

	private ProductDTO applyPromotion(ProductDTO dto, Long productId, LocalDateTime now) {
		if (dto == null || productId == null)
			return dto;

		dto.setPromoActive(false);
		dto.setPromoName(null);
		dto.setDiscountPercent(null);

		double basePrice = dto.getPrice() != null ? dto.getPrice() : 0.0;
		dto.setOldPrice(basePrice);
		dto.setDiscountedPrice(basePrice);

		promoProductRepo.findActiveForProductAt(productId, now).ifPresent(pp -> {
			int percent = pp.getDiscountPercent();

			dto.setPromoActive(true);
			dto.setPromoName(pp.getPromotion().getName());
			dto.setDiscountPercent(percent);

			dto.setOldPrice(basePrice);
			dto.setDiscountedPrice(round2(basePrice * (1.0 - percent / 100.0)));
		});

		return dto;
	}

	private double round2(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	private void syncStockAlert(Product p) {
		if (p == null || p.getId() == null)
			return;

		if (p.getCategory() == ProductCategory.USATO_RICONDIZIONATO)
			return;

		int qty = p.getQuantity();

		if (qty <= LOW_STOCK_THRESHOLD) {
			stockAlertRepo.findTop1ByProduct_IdAndStatusOrderByCreatedAtDesc(p.getId(), AlertStatus.OPEN)
					.ifPresentOrElse(alert -> {
						alert.setCurrentQuantity(qty);
						alert.setProductName(p.getName());
						alert.setThreshold(LOW_STOCK_THRESHOLD);
						stockAlertRepo.save(alert);
					}, () -> {
						StockAlert a = new StockAlert();
						a.setCreatedAt(LocalDateTime.now());
						a.setProduct(p);
						a.setProductName(p.getName());
						a.setCurrentQuantity(qty);
						a.setThreshold(LOW_STOCK_THRESHOLD);
						a.setStatus(AlertStatus.OPEN);
						stockAlertRepo.save(a);
					});
			return;
		}

		stockAlertRepo.findTop1ByProduct_IdAndStatusOrderByCreatedAtDesc(p.getId(), AlertStatus.OPEN)
				.ifPresent(alert -> {
					alert.setCurrentQuantity(qty);
					alert.setStatus(AlertStatus.ACK);
					stockAlertRepo.save(alert);
				});
	}

	@Override
	@Transactional
	public ProductDTO restock(Long id, int addQuantity) {
		if (addQuantity == 0) {
			throw new BadRequestException("addQuantity non può essere 0");
		}

		Product p = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + id));

		int newQty = p.getQuantity() + addQuantity;
		if (newQty < 0) {
			throw new BadRequestException(
					"Stock non può diventare negativo. Attuale: " + p.getQuantity() + ", variazione: " + addQuantity);
		}

		p.setQuantity(newQty);

		syncStockAlert(p);

		Product saved = productRepository.save(p);

		broadcaster.publishInventoryChanged("PRODUCT_RESTOCK");

		LocalDateTime now = LocalDateTime.now();
		return applyPromotion(DtoMapper.toProductDto(saved), saved.getId(), now);
	}
}
