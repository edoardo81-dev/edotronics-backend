package com.example.repository;

import com.example.model.Product;
import com.example.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByCategoryAndNameContainingIgnoreCase(ProductCategory category, String name, Pageable pageable);


    @Query("""
        select p.id as productId,
               p.name as name,
               coalesce(sum(op.orderedQuantity), 0) as totalSold
        from Product p
        left join p.orderProducts op
        group by p.id, p.name
        order by coalesce(sum(op.orderedQuantity), 0) desc
    """)
    List<ProductSalesView> topSelling(Pageable pageable);

    @Query("""
        select p.id as productId,
               p.name as name,
               coalesce(sum(op.orderedQuantity), 0) as totalSold
        from Product p
        left join p.orderProducts op
        group by p.id, p.name
        order by coalesce(sum(op.orderedQuantity), 0) asc
    """)
    List<ProductSalesView> leastSelling(Pageable pageable);


    @Query("""
        select p.id as productId,
               p.name as name,
               coalesce(sum(
                   case
                     when (o is not null and o.dateTime >= :from) then op.orderedQuantity
                     else 0
                   end
               ), 0) as totalSold
        from Product p
        left join p.orderProducts op
        left join op.order o
        group by p.id, p.name
        order by coalesce(sum(
                   case
                     when (o is not null and o.dateTime >= :from) then op.orderedQuantity
                     else 0
                   end
               ), 0) desc
    """)
    List<ProductSalesView> topSellingSince(@Param("from") LocalDateTime from, Pageable pageable);

    @Query("""
        select p.id as productId,
               p.name as name,
               coalesce(sum(
                   case
                     when (o is not null and o.dateTime >= :from) then op.orderedQuantity
                     else 0
                   end
               ), 0) as totalSold
        from Product p
        left join p.orderProducts op
        left join op.order o
        group by p.id, p.name
        order by coalesce(sum(
                   case
                     when (o is not null and o.dateTime >= :from) then op.orderedQuantity
                     else 0
                   end
               ), 0) asc
    """)
    List<ProductSalesView> leastSellingSince(@Param("from") LocalDateTime from, Pageable pageable);
}
