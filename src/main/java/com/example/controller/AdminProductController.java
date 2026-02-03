package com.example.controller;

import com.example.dto.CreateProductRequestDTO;
import com.example.dto.ProductDTO;
import com.example.dto.RestockRequestDTO;
import com.example.dto.UpdateProductRequestDTO;
import com.example.model.ProductCategory;
import com.example.repository.ProductSalesView;
import com.example.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductDTO> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ProductCategory category,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return productService.getAll(q, category, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/stats/top-selling")
    public List<ProductSalesView> topSelling(
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(required = false) Integer days
    ) {
        return productService.topSelling(limit, days);
    }

    @GetMapping("/stats/least-selling")
    public List<ProductSalesView> leastSelling(
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(required = false) Integer days
    ) {
        return productService.leastSelling(limit, days);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody CreateProductRequestDTO dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequestDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restock")
    public ResponseEntity<ProductDTO> restock(@PathVariable Long id, @Valid @RequestBody RestockRequestDTO req) {
        return ResponseEntity.ok(productService.restock(id, req.getAddQuantity()));
    }
}
