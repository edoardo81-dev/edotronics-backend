package com.example.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionUpdateRequestDTO {
    private String name;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private Boolean active;
    private Boolean archived;

}
