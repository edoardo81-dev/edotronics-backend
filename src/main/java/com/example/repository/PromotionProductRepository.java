package com.example.repository;

import com.example.model.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    @Query("""
        select pp
        from PromotionProduct pp
        join fetch pp.promotion p
        where pp.product.id = :productId
          and p.active = true
          and :now between p.startsAt and p.endsAt
        """)
    Optional<PromotionProduct> findActiveForProductAt(
            @Param("productId") Long productId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        select count(pp) 
        from PromotionProduct pp
        join pp.promotion p
        where pp.product.id = :productId
          and p.active = true
          and p.startsAt < :endsAt
          and p.endsAt > :startsAt
        """)
    long countActiveOverlaps(
            @Param("productId") Long productId,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt
    );

    @Query("""
        select count(pp) 
        from PromotionProduct pp
        join pp.promotion p
        where pp.product.id = :productId
          and p.active = true
          and p.id <> :promotionId
          and p.startsAt < :endsAt
          and p.endsAt > :startsAt
        """)
    long countActiveOverlapsExcludingPromotion(
            @Param("productId") Long productId,
            @Param("promotionId") Long promotionId,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt
    );
}
