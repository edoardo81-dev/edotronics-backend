package com.example.dto;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class PromotionResponseDTO {
    private Long id;
    private String name;
    private String startsAt;
    private String endsAt;
    private boolean active;
    private boolean archived;

    private List<PromotionItemResponseDTO> items;
}
