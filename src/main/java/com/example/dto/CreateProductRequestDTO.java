package com.example.dto;

import com.example.model.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequestDTO {

    @NotBlank(message = "nome obbligatorio")
    private String name;

    @NotNull(message = "prezzo obbligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "prezzo deve essere >= 0")
    private Double price;

    @Min(value = 0, message = "quantità deve essere >= 0")
    private int quantity;

    @NotBlank(message = "imageUrl obbligatorio")
    private String imageUrl;

    @NotNull(message = "category obbligatoria")
    private ProductCategory category;
}
