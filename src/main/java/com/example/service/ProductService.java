package com.example.service;

import com.example.dto.CreateProductRequestDTO;
import com.example.dto.ProductDTO;
import com.example.dto.UpdateProductRequestDTO;
import com.example.model.ProductCategory;
import com.example.repository.ProductSalesView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Page<ProductDTO> getAll(String q, ProductCategory category, Pageable pageable);

    ProductDTO getById(Long id);

    ProductDTO create(CreateProductRequestDTO dto);

    ProductDTO update(Long id, UpdateProductRequestDTO dto);

    void delete(Long id);

    ProductDTO restock(Long id, int addQuantity);

    List<ProductCategory> getCategories();

    List<ProductSalesView> topSelling(int limit);
    List<ProductSalesView> leastSelling(int limit);

    List<ProductSalesView> topSelling(int limit, Integer days);
    List<ProductSalesView> leastSelling(int limit, Integer days);
}
