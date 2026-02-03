package com.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_product")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference("order-orderProducts")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference("product-orderProducts")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    private int orderedQuantity;

    @Column(nullable = false)
    private double unitPriceAtPurchase; 
}
