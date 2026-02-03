package com.example.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class PromotionItemResponseDTO {
    private Long productId;
    private String productName;
    private int discountPercent;
}
