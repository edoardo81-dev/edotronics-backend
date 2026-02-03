package com.example.controller;

import com.example.dto.PromotionCreateRequestDTO;
import com.example.dto.PromotionResponseDTO;
import com.example.dto.PromotionUpdateRequestDTO;
import com.example.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public PromotionResponseDTO create(@Valid @RequestBody PromotionCreateRequestDTO req) {
        return promotionService.create(req);
    }

    @GetMapping
    public List<PromotionResponseDTO> getAll(
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {
        return promotionService.getAll(includeArchived);
    }

    @GetMapping("/{id}")
    public PromotionResponseDTO getById(@PathVariable Long id) {
        return promotionService.getById(id);
    }

    @PutMapping("/{id}")
    public PromotionResponseDTO update(@PathVariable Long id, @RequestBody PromotionUpdateRequestDTO req) {
        return promotionService.update(id, req);
    }

    @PostMapping("/{id}/archive")
    public PromotionResponseDTO archive(@PathVariable Long id) {
        return promotionService.archive(id);
    }
}
