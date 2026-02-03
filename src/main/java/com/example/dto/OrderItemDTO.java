package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    @NotNull(message = "productId obbligatorio")
    private Long productId;

    @Min(value = 1, message = "orderedQuantity deve essere >= 1")
    private int orderedQuantity;

    private String name;
    private double price;
}
