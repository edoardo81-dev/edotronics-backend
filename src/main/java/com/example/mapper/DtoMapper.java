package com.example.mapper;

import com.example.dto.*;
import com.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    /* ===================== ORDER ===================== */
    public static OrderDTO toDto(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setIdOrder(order.getIdOrder());
        dto.setDateTime(order.getDateTime() != null ? order.getDateTime().toString() : null);

        dto.setDescription(order.getDescription());

        if (order.getUser() != null) {
            dto.setIdUser(order.getUser().getIdUser());
        }

        dto.setFirstName(order.getShipFirstName());
        dto.setLastName(order.getShipLastName());
        dto.setPhone(order.getShipPhone());
        dto.setAddress(order.getShipAddress());
        dto.setCity(order.getShipCity());

        if (order.getOrderProducts() != null) {
            List<OrderItemDTO> items = order.getOrderProducts().stream()
                    .map(op -> new OrderItemDTO(
                            op.getProduct().getId(),
                            op.getOrderedQuantity(),
                            op.getProduct().getName(),
                            op.getUnitPriceAtPurchase() // ✅ prezzo congelato
                    ))
                    .collect(Collectors.toList());
            dto.setProducts(items);
        }

        return dto;
    }

    public static List<OrderDTO> toOrderDtoList(List<Order> orders) {
        return orders.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }

    /* ===================== PERSONA ===================== */
    public static PersonaDTO toDto(Persona persona) {
        if (persona == null) return null;
        PersonaDTO dto = new PersonaDTO();
        dto.setIdUser(persona.getIdUser());
        dto.setFirstName(persona.getFirstName());
        dto.setLastName(persona.getLastName());
        dto.setEmail(persona.getEmail());
        dto.setPhone(persona.getPhone());
        dto.setAddress(persona.getAddress());
        dto.setCity(persona.getCity());
        return dto;
    }

    public static Persona toEntity(PersonaDTO dto) {
        if (dto == null) return null;
        Persona p = new Persona();
        p.setIdUser(dto.getIdUser());
        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setEmail(dto.getEmail());
        p.setPhone(dto.getPhone());
        p.setAddress(dto.getAddress());
        p.setCity(dto.getCity());

        p.setActive(true);
        p.setAge(null);
        p.setOrders(new ArrayList<>());
        return p;
    }

    public static List<PersonaDTO> toPersonaDtoList(List<Persona> persone) {
        return persone.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }

    /* ===================== PRODUCT ===================== */
    public static ProductDTO toProductDto(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(p.getId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        dto.setImageUrl(p.getImageUrl());
        dto.setCategory(p.getCategory());
        return dto;
    }

    public static List<ProductDTO> toProductDtoList(List<Product> products) {
        return products.stream().map(DtoMapper::toProductDto).collect(Collectors.toList());
    }

    public static Product toEntity(ProductDTO dto) {
        Product p = new Product();
        p.setId(dto.getProductId());
        p.setName(dto.getName());
        p.setPrice(dto.getPrice() != null ? dto.getPrice() : 0.0);
        p.setQuantity(dto.getQuantity());
        p.setImageUrl(dto.getImageUrl());
        p.setCategory(dto.getCategory());
        return p;
    }

    /* ===================== STOCK ALERT ===================== */
    public static StockAlertDTO toDto(StockAlert alert) {
        if (alert == null) return null;

        StockAlertDTO dto = new StockAlertDTO();
        dto.setId(alert.getId());
        dto.setCreatedAt(alert.getCreatedAt() != null ? alert.getCreatedAt().toString() : null);

        if (alert.getProduct() != null) {
            dto.setProductId(alert.getProduct().getId());
        }

        dto.setProductName(alert.getProductName());
        dto.setCurrentQuantity(alert.getCurrentQuantity());
        dto.setThreshold(alert.getThreshold());
        dto.setStatus(alert.getStatus());

        return dto;
    }

    public static List<StockAlertDTO> toStockAlertDtoList(List<StockAlert> alerts) {
        return alerts.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }
}
