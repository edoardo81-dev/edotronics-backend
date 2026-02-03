package com.example.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class PromotionItemRequestDTO {

    @NotNull(message="productId obbligatorio")
    private Long productId;

    @Min(value=1, message="discountPercent >= 1")
    @Max(value=90, message="discountPercent <= 90")
    private int discountPercent;
}
