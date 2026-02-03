package com.example.service.impl;

import com.example.dto.*;
import com.example.exception.BadRequestException;
import com.example.exception.ConflictException;
import com.example.exception.NotFoundException;
import com.example.model.Product;
import com.example.model.Promotion;
import com.example.model.PromotionProduct;
import com.example.repository.ProductRepository;
import com.example.repository.PromotionProductRepository;
import com.example.repository.PromotionRepository;
import com.example.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

	private final PromotionRepository promotionRepo;
	private final PromotionProductRepository promoProductRepo;
	private final ProductRepository productRepo;

	@Override
	@Transactional
	public PromotionResponseDTO create(PromotionCreateRequestDTO req) {

		String name = req.getName() != null ? req.getName().trim() : null;
		if (name == null || name.isBlank()) {
			throw new BadRequestException("name obbligatorio");
		}

		if (req.getStartsAt() == null || req.getEndsAt() == null) {
			throw new BadRequestException("startsAt/endsAt obbligatori");
		}
		if (!req.getEndsAt().isAfter(req.getStartsAt())) {
			throw new BadRequestException("endsAt deve essere dopo startsAt");
		}

		if (req.isActive()) {
			for (PromotionItemRequestDTO item : req.getItems()) {
				long overlaps = promoProductRepo.countActiveOverlaps(item.getProductId(), req.getStartsAt(),
						req.getEndsAt());
				if (overlaps > 0) {
					throw new ConflictException(
							"Prodotto " + item.getProductId() + " già in un'altra promozione attiva sovrapposta");
				}
			}
		}

		Promotion promo = new Promotion();
		promo.setName(name);
		promo.setStartsAt(req.getStartsAt());
		promo.setEndsAt(req.getEndsAt());
		promo.setActive(req.isActive());

		promo.setArchived(false);

		promo.setItems(new ArrayList<>());

		Promotion savedPromo = promotionRepo.save(promo);

		for (PromotionItemRequestDTO item : req.getItems()) {
			Product product = productRepo.findById(item.getProductId())
					.orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + item.getProductId()));

			PromotionProduct pp = new PromotionProduct();
			pp.setPromotion(savedPromo);
			pp.setProduct(product);
			pp.setDiscountPercent(item.getDiscountPercent());

			savedPromo.getItems().add(pp);
		}

		Promotion savedFull = promotionRepo.save(savedPromo);
		return toResponse(savedFull);
	}

	@Override
	public List<PromotionResponseDTO> getAll(boolean includeArchived) {
		List<Promotion> list = promotionRepo.findAll(); // EntityGraph già ok
		return list.stream().filter(p -> includeArchived || !Boolean.TRUE.equals(p.isArchived())).map(this::toResponse)
				.toList();
	}

	@Override
	public PromotionResponseDTO getById(Long id) {
		Promotion p = promotionRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("Promozione non trovata: " + id));
		return toResponse(p);
	}

	@Override
	@Transactional
	public PromotionResponseDTO update(Long id, PromotionUpdateRequestDTO req) {

		Promotion promo = promotionRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("Promozione non trovata: " + id));

		if (Boolean.TRUE.equals(promo.isArchived())) {
			throw new BadRequestException("Promozione archiviata: non modificabile");
		}

		String newName = (req.getName() != null) ? req.getName().trim() : promo.getName();
		LocalDateTime newStarts = (req.getStartsAt() != null) ? req.getStartsAt() : promo.getStartsAt();
		LocalDateTime newEnds = (req.getEndsAt() != null) ? req.getEndsAt() : promo.getEndsAt();
		boolean newActive = (req.getActive() != null) ? req.getActive() : promo.isActive();

		if (newName == null || newName.isBlank()) {
			throw new BadRequestException("name obbligatorio");
		}
		if (newStarts == null || newEnds == null) {
			throw new BadRequestException("startsAt/endsAt obbligatori");
		}
		if (!newEnds.isAfter(newStarts)) {
			throw new BadRequestException("endsAt deve essere dopo startsAt");
		}

		if (newActive && promo.getItems() != null) {
			for (PromotionProduct pp : promo.getItems()) {
				Long productId = pp.getProduct().getId();
				long overlaps = promoProductRepo.countActiveOverlapsExcludingPromotion(productId, promo.getId(),
						newStarts, newEnds);
				if (overlaps > 0) {
					throw new ConflictException(
							"Prodotto " + productId + " già in un'altra promozione attiva sovrapposta");
				}
			}
		}

		promo.setName(newName);
		promo.setStartsAt(newStarts);
		promo.setEndsAt(newEnds);
		promo.setActive(newActive);

		Promotion saved = promotionRepo.save(promo);
		return toResponse(saved);
	}

	@Override
	@Transactional
	public PromotionResponseDTO archive(Long id) {
		Promotion promo = promotionRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("Promozione non trovata: " + id));

		if (Boolean.TRUE.equals(promo.isArchived())) {
			return toResponse(promo); // già archiviata
		}

		promo.setActive(false);
		promo.setArchived(true);

		Promotion saved = promotionRepo.save(promo);
		return toResponse(saved);
	}

	private PromotionResponseDTO toResponse(Promotion p) {
		PromotionResponseDTO dto = new PromotionResponseDTO();
		dto.setId(p.getId());
		dto.setName(p.getName());
		dto.setStartsAt(p.getStartsAt() != null ? p.getStartsAt().toString() : null);
		dto.setEndsAt(p.getEndsAt() != null ? p.getEndsAt().toString() : null);
		dto.setActive(p.isActive());

		dto.setArchived(Boolean.TRUE.equals(p.isArchived()));

		List<PromotionItemResponseDTO> items = new ArrayList<>();
		if (p.getItems() != null) {
			for (PromotionProduct pp : p.getItems()) {
				items.add(new PromotionItemResponseDTO(pp.getProduct().getId(), pp.getProduct().getName(),
						pp.getDiscountPercent()));
			}
		}
		dto.setItems(items);
		return dto;
	}
}
