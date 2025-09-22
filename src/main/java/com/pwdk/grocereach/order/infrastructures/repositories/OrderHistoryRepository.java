package com.pwdk.grocereach.order.infrastructures.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.pwdk.grocereach.order.presentations.dtos.sales.MonthlyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pwdk.grocereach.order.domains.entities.OrderHistory;
import com.pwdk.grocereach.order.presentations.dtos.sales.OrderSummaryRow;
import com.pwdk.grocereach.order.presentations.dtos.sales.OrderItemRow;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {

  @Query(value = "\n" +
      "SELECT oh.updated_at AS updated_at, o.id AS order_id, CAST(oh.status AS TEXT) AS status, \n" +
      "       s.id AS store_id, s.store_name AS store_name, \n" +
      "       SUM(oi.quantity * oi.price) AS total_revenue \n" +
      "FROM order_history oh \n" +
      "JOIN orders o ON o.id = oh.order_id \n" +
      "JOIN order_items oi ON oi.order_id = o.id \n" +
      "JOIN product p ON p.id = oi.product_id \n" +
      "LEFT JOIN product_category pc ON pc.id = p.category_id \n" +
      "JOIN stores s ON s.id = p.store_id \n" +
      "WHERE oh.deleted_at IS NULL \n" +
      "  AND (:storeId IS NULL OR s.id = :storeId) \n" +
      "  AND (:categoryId IS NULL OR pc.id = :categoryId) \n" +
      "  AND (:productId IS NULL OR p.id = :productId) \n" +
      "  AND oh.updated_at >= :startDate AND oh.updated_at < :endDate \n" +
      "GROUP BY oh.updated_at, o.id, oh.status, s.id, s.store_name \n" +
      "ORDER BY oh.updated_at DESC\n",
      countQuery = "\n" +
          "SELECT COUNT(DISTINCT o.id) FROM order_history oh \n" +
          "JOIN orders o ON o.id = oh.order_id \n" +
          "JOIN order_items oi ON oi.order_id = o.id \n" +
          "JOIN product p ON p.id = oi.product_id \n" +
          "LEFT JOIN product_category pc ON pc.id = p.category_id \n" +
          "JOIN stores s ON s.id = p.store_id \n" +
          "WHERE oh.deleted_at IS NULL \n" +
          "  AND (:storeId IS NULL OR s.id = :storeId) \n" +
          "  AND (:categoryId IS NULL OR pc.id = :categoryId) \n" +
          "  AND (:productId IS NULL OR p.id = :productId) \n" +
          "  AND oh.updated_at >= :startDate AND oh.updated_at < :endDate\n",
      nativeQuery = true)
  Page<OrderSummaryRow> findOrderHistoryReport(
      @Param("storeId") UUID storeId,
      @Param("categoryId") UUID categoryId,
      @Param("productId") UUID productId,
      @Param("startDate") Instant startDate,
      @Param("endDate") Instant endDate,
      Pageable pageable
  );

  @Query(value = "\n" +
      "SELECT o.id AS order_id, p.id AS product_id, p.name AS product_name, \n" +
      "       pc.id AS category_id, pc.name AS category_name, \n" +
      "       oi.quantity AS quantity, oi.price AS price, (oi.quantity * oi.price) AS revenue \n" +
      "FROM orders o \n" +
      "JOIN order_items oi ON oi.order_id = o.id \n" +
      "JOIN product p ON p.id = oi.product_id \n" +
      "LEFT JOIN product_category pc ON pc.id = p.category_id \n" +
      "WHERE o.id IN (:orderIds) \n" +
      "ORDER BY p.name ASC\n",
      nativeQuery = true)
  List<OrderItemRow> findItemsForOrders(@Param("orderIds") List<UUID> orderIds);

  @Query(value = """
    SELECT
        to_char(oh.updated_at, 'YYYY-MM') AS month,
        COUNT(DISTINCT o.id) AS orderCount
    FROM order_history oh
    JOIN orders o ON o.id = oh.order_id
    WHERE oh.deleted_at IS NULL
      AND date_part('year', oh.updated_at) = date_part('year', CURRENT_DATE)
    GROUP BY to_char(oh.updated_at, 'YYYY-MM')
    ORDER BY month
    """,
      nativeQuery = true)
  List<MonthlyOrder> findMonthlyOrderCounts();

}