package com.pwdk.grocereach.inventory.infrastructures.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.pwdk.grocereach.inventory.domains.interfaces.InventoryMonthlyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query(value = "SELECT i.* FROM inventory i WHERE i.warehouse_id = :warehouseId AND i.product_version_id = :productVersionId AND i.deleted_at IS NULL ORDER BY i.created_at DESC LIMIT 1", nativeQuery = true)
    Inventory findLatestByWarehouseAndProductVersion(@Param("warehouseId") UUID warehouseId, @Param("productVersionId") UUID productVersionId);

    Inventory findTopByWarehouse_IdAndProductVersion_IdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID warehouseId, UUID productVersionId);

    @Query(value = """
    WITH latest_version_per_warehouse AS (
        SELECT 
            p.id as product_id,
            w.id as warehouse_id,
            to_char(i.created_at, 'YYYY-MM') as month,
            MAX(pv.version_number) as latest_version
        FROM inventory i
        JOIN warehouses w ON w.id = i.warehouse_id
        JOIN stores s ON s.id = w.store_id
        JOIN product_version pv ON pv.id = i.product_version_id
        JOIN product p ON p.id = pv.product_id
        WHERE p.deleted_at IS NULL
          AND (:storeId IS NULL OR s.id = :storeId)
          AND (:warehouseId IS NULL OR w.id = :warehouseId)
          AND (:productName IS NULL OR p.name ILIKE CONCAT('%', :productName, '%'))
          AND i.created_at >= :startDate
          AND i.created_at < :endDate
          AND NOT (i.journal ILIKE '%Version migration:%')
        GROUP BY p.id, w.id, to_char(i.created_at, 'YYYY-MM')
    ),
    latest_stock AS (
        SELECT 
            lv.product_id,
            lv.warehouse_id,
            lv.month,
            lv.latest_version,
            i.stock as final_stock
        FROM latest_version_per_warehouse lv
        JOIN product_version pv ON pv.product_id = lv.product_id AND pv.version_number = lv.latest_version
        JOIN inventory i ON i.product_version_id = pv.id AND i.warehouse_id = lv.warehouse_id
        WHERE to_char(i.created_at, 'YYYY-MM') = lv.month
          AND i.created_at = (
              SELECT MAX(i2.created_at)
              FROM inventory i2
              WHERE i2.product_version_id = pv.id
                AND i2.warehouse_id = lv.warehouse_id
                AND to_char(i2.created_at, 'YYYY-MM') = lv.month
          )
    )
    SELECT
        p.name AS productName,
        p.id AS productId,
        w.name AS warehouseName,
        w.id AS warehouseId,
        s.store_name AS storeName,
        to_char(i.created_at, 'YYYY-MM') AS month,
        ls.latest_version AS latestVersion,
        SUM(
            CASE
                WHEN i.journal LIKE '+%' THEN
                     CAST(regexp_replace(i.journal, '[^0-9]', '', 'g') AS INTEGER)
                ELSE 0
            END
        ) AS totalAddition,
        SUM(
            CASE
                WHEN i.journal LIKE '-%' THEN
                     CAST(regexp_replace(i.journal, '[^0-9]', '', 'g') AS INTEGER)
                ELSE 0
            END
        ) AS totalReduction,
        ls.final_stock AS finalStock,
        AVG(pv.price) AS averagePrice
    FROM inventory i
    JOIN warehouses w ON w.id = i.warehouse_id
    JOIN stores s ON s.id = w.store_id
    JOIN product_version pv ON pv.id = i.product_version_id
    JOIN product p ON p.id = pv.product_id
    JOIN latest_stock ls ON (
        ls.product_id = p.id 
        AND ls.warehouse_id = w.id 
        AND ls.month = to_char(i.created_at, 'YYYY-MM')
    )
    WHERE p.deleted_at IS NULL
      AND (:storeId IS NULL OR s.id = :storeId)
      AND (:warehouseId IS NULL OR w.id = :warehouseId)
      AND (:productName IS NULL OR p.name ILIKE CONCAT('%', :productName, '%'))
      AND i.created_at >= :startDate
      AND i.created_at < :endDate
      AND NOT (i.journal ILIKE '%Version migration:%')
    GROUP BY
        p.name,
        p.id,
        w.name,
        w.id,
        s.store_name,
        to_char(i.created_at, 'YYYY-MM'),
        ls.latest_version,
        ls.final_stock
    ORDER BY
        p.name,
        w.name,
        month
    """,
        nativeQuery = true)
    List<InventoryMonthlyReport> findAggregatedInventoryMonthlyReport(
        @Param("storeId") UUID storeId,
        @Param("warehouseId") UUID warehouseId,
        @Param("productName") String productName,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );

    @Query(value = """
    SELECT i.* 
    FROM inventory i
    JOIN warehouses w ON w.id = i.warehouse_id
    JOIN stores s ON s.id = w.store_id
    JOIN product_version pv ON pv.id = i.product_version_id
    JOIN product p ON p.id = pv.product_id
    WHERE (:storeId IS NULL OR s.id = :storeId)
      AND (:warehouseId IS NULL OR w.id = :warehouseId)
      AND (:productId IS NULL OR p.id = :productId)
      AND i.created_at >= :startDate
      AND i.created_at < :endDate
    ORDER BY p.name, pv.version_number, i.created_at
    """,
        nativeQuery = true
    )
    List<Inventory> findInventoryByProduct(
        @Param("productId") UUID productId,
        @Param("storeId") UUID storeId,
        @Param("warehouseId") UUID warehouseId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );
}
