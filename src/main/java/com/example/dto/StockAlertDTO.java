package com.example.dto;

import com.example.model.AlertStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id",
        "createdAt",
        "productId",
        "productName",
        "currentQuantity",
        "threshold",
        "status"
})
public class StockAlertDTO {

    private Long id;
    private String createdAt;

    private Long productId;
    private String productName;

    private int currentQuantity;
    private int threshold;

    private AlertStatus status;
}
