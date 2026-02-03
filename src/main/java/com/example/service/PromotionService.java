package com.example.service;

import com.example.dto.PromotionCreateRequestDTO;
import com.example.dto.PromotionResponseDTO;
import com.example.dto.PromotionUpdateRequestDTO;

import java.util.List;

public interface PromotionService {

    PromotionResponseDTO create(PromotionCreateRequestDTO req);

    List<PromotionResponseDTO> getAll(boolean includeArchived);

    PromotionResponseDTO getById(Long id);

    PromotionResponseDTO update(Long id, PromotionUpdateRequestDTO req);

    PromotionResponseDTO archive(Long id);
}
