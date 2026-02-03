package com.example.dto;

import com.example.model.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDTO {

    @NotBlank(message = "Nome obbligatorio")
    private String name;

    @NotNull(message = "Prezzo obbligatorio")
    @DecimalMin(value = "0.01", message = "Il prezzo deve essere > 0")
    private Double price;

    @NotBlank(message = "Image URL obbligatoria")
    private String imageUrl;

    @NotNull(message = "Categoria obbligatoria")
    private ProductCategory category;
}
