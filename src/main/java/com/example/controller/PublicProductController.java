package com.example.controller;

import com.example.dto.ProductDTO;
import com.example.model.ProductCategory;
import com.example.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
public class PublicProductController {

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
    public ProductDTO getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @GetMapping("/categories")
    public List<ProductCategory> categories() {
        return productService.getCategories();
    }
}
