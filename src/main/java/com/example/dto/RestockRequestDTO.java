package com.example.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockRequestDTO {

    @NotNull(message = "addQuantity obbligatorio")
    @Min(value = -1000000, message = "addQuantity troppo piccolo")
    @Max(value = 1000000, message = "addQuantity troppo grande")
    private Integer addQuantity;
}
