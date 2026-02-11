package com.fypbackend.spring_boot.dao;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fypbackend.spring_boot.entity.Order;

@RepositoryRestResource
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerEmailOrderByDateCreatedDesc(@Param("email") String email, Pageable pageable);

    Page<Order> findByStatusIgnoreCase(String status, Pageable pageable);

    @Query(value = "SELECT DATE_FORMAT(o.date_created, '%Y-%m-%d') AS period, COALESCE(SUM(o.total_price), 0) AS total "
            + "FROM orders o "
            + "WHERE o.date_created >= :start AND o.date_created < :end "
            + "GROUP BY period "
            + "ORDER BY period",
            nativeQuery = true)
    List<SalesPeriodAggregation> sumDailySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT DATE_FORMAT(o.date_created, '%x-W%v') AS period, COALESCE(SUM(o.total_price), 0) AS total "
            + "FROM orders o "
            + "WHERE o.date_created >= :start AND o.date_created < :end "
            + "GROUP BY period "
            + "ORDER BY period",
            nativeQuery = true)
    List<SalesPeriodAggregation> sumWeeklySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT DATE_FORMAT(o.date_created, '%Y-%m') AS period, COALESCE(SUM(o.total_price), 0) AS total "
            + "FROM orders o "
            + "WHERE o.date_created >= :start AND o.date_created < :end "
            + "GROUP BY period "
            + "ORDER BY period",
            nativeQuery = true)
    List<SalesPeriodAggregation> sumMonthlySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT DATE_FORMAT(o.date_created, '%Y-%m-%d') AS period, COUNT(*) AS count "
            + "FROM orders o "
            + "WHERE o.date_created >= :start AND o.date_created < :end "
            + "GROUP BY period "
            + "ORDER BY period",
            nativeQuery = true)
    List<OrderCountPeriodAggregation> countDailyOrders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT DATE_FORMAT(o.date_created, '%x-W%v') AS period, COUNT(*) AS count "
            + "FROM orders o "
            + "WHERE o.date_created >= :start AND o.date_created < :end "
            + "GROUP BY period "
            + "ORDER BY period",
            nativeQuery = true)
    List<OrderCountPeriodAggregation> countWeeklyOrders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT p.id AS productId, p.name AS productName, SUM(oi.quantity) AS unitsSold, "
            + "SUM(oi.quantity * oi.unit_price) AS revenue "
            + "FROM order_item oi "
            + "JOIN orders o ON o.id = oi.order_id "
            + "JOIN product p ON p.id = oi.product_id "
            + "WHERE o.date_created >= :start AND o.date_created < :end "
            + "GROUP BY p.id, p.name "
            + "ORDER BY unitsSold DESC",
            nativeQuery = true)
    List<TopSellingProductProjection> findTopSellingProducts(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) AS totalSales, COUNT(o) AS totalOrders, COALESCE(AVG(o.totalPrice), 0) AS avgOrderValue "
            + "FROM Order o "
            + "WHERE o.dateCreated >= :start AND o.dateCreated < :end")
    KpiProjection summarizeKpis(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
