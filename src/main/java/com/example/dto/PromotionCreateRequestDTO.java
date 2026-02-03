package com.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class PromotionCreateRequestDTO {

    @NotBlank(message="name obbligatorio")
    private String name;

    @NotNull(message="startsAt obbligatorio")
    private LocalDateTime startsAt;

    @NotNull(message="endsAt obbligatorio")
    private LocalDateTime endsAt;

    private boolean active = true;

    @NotEmpty(message="items deve contenere almeno 1 prodotto")
    @Valid
    private List<PromotionItemRequestDTO> items;
}
