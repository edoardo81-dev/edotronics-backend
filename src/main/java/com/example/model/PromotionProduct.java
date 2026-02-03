package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "promotion_products",
    uniqueConstraints = @UniqueConstraint(name="uk_promo_product", columnNames = {"promotion_id","product_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PromotionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="promotion_id")
    private Promotion promotion;

    @ManyToOne(optional=false)
    @JoinColumn(name="product_id")
    private Product product;

    @Column(nullable=false)
    private int discountPercent; 
}
