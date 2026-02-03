package com.example.repository;

import com.example.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "orderProducts", "orderProducts.product"})
    Page<Order> findByUser_IdUser(Long idUser, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderProducts", "orderProducts.product"})
    @Query(
            value = """
                select distinct o
                from Order o
                left join o.user u
                left join o.orderProducts op
                left join op.product p
                where (
                    :customer is null
                    or lower(u.firstName) like lower(concat('%', :customer, '%'))
                    or lower(u.lastName)  like lower(concat('%', :customer, '%'))
                    or lower(concat(concat(u.firstName, ' '), u.lastName)) like lower(concat('%', :customer, '%'))
                    or lower(concat(concat(u.lastName, ' '), u.firstName)) like lower(concat('%', :customer, '%'))
                )
                  and (:product is null or
                      lower(p.name) like lower(concat('%', :product, '%')))
                  and (:city is null or
                      lower(u.city) like lower(concat('%', :city, '%')))
                  and (:from is null or o.dateTime >= :from)
                  and (:to is null or o.dateTime <= :to)
                """,
            countQuery = """
                select count(distinct o.idOrder)
                from Order o
                left join o.user u
                left join o.orderProducts op
                left join op.product p
                where (
                    :customer is null
                    or lower(u.firstName) like lower(concat('%', :customer, '%'))
                    or lower(u.lastName)  like lower(concat('%', :customer, '%'))
                    or lower(concat(concat(u.firstName, ' '), u.lastName)) like lower(concat('%', :customer, '%'))
                    or lower(concat(concat(u.lastName, ' '), u.firstName)) like lower(concat('%', :customer, '%'))
                )
                  and (:product is null or
                      lower(p.name) like lower(concat('%', :product, '%')))
                  and (:city is null or
                      lower(u.city) like lower(concat('%', :city, '%')))
                  and (:from is null or o.dateTime >= :from)
                  and (:to is null or o.dateTime <= :to)
                """
    )
    Page<Order> adminSearch(
            @Param("customer") String customer,
            @Param("product") String product,
            @Param("city") String city,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "orderProducts", "orderProducts.product"})
    Page<Order> findAll(Pageable pageable);
}
