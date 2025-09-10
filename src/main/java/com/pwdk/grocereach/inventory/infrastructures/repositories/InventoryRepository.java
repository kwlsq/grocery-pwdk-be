package com.pwdk.grocereach.inventory.infrastructures.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query(value = "SELECT DISTINCT ON (i.warehouse_id, p.id) i.* " +
        "FROM inventory i " +
        "JOIN warehouses w ON w.id = i.warehouse_id " +
        "JOIN stores s ON s.id = w.store_id " +
        "JOIN product_version pv ON pv.id = i.product_version_id " +
        "JOIN product p ON p.id = pv.product_id " +
        "WHERE i.deleted_at IS NULL " +
        "AND (:storeId IS NULL OR s.id = :storeId) " +
        "AND (:warehouseId IS NULL OR w.id = :warehouseId) " +
        "AND (:productName IS NULL OR p.name ILIKE CONCAT('%', :productName, '%')) " +
        "AND i.created_at >= :startDate " +
        "AND i.created_at < :endDate " +
        "ORDER BY i.warehouse_id, p.id, i.created_at DESC",
        nativeQuery = true)
    Page<Inventory> findInventoryHistoryForReport(
        @Param("storeId") UUID storeId,
        @Param("warehouseId") UUID warehouseId,
        @Param("productName") String productName,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate,
        Pageable pageable
    );

    @Query("SELECT i FROM Inventory i " +
        "JOIN i.warehouse w " +
        "JOIN w.store s " +
        "JOIN i.productVersion pv " +
        "JOIN pv.product p " +
        "WHERE (:storeId IS NULL OR s.id = :storeId) " +
        "AND (:warehouseId IS NULL OR w.id = :warehouseId) " +
        "AND (:productName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%'))) " +
        "AND i.createdAt >= :startDate " +
        "AND i.createdAt < :endDate " +
        "ORDER BY p.name, pv.versionNumber")
    Page<Inventory> findCurrentInventoryForReport(
        @Param("storeId") UUID storeId,
        @Param("warehouseId") UUID warehouseId,
        @Param("productName") String productName,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate,
        Pageable pageable
    );

    @Query(value = "SELECT i.* FROM inventory i " +
        "JOIN warehouses w ON w.id = i.warehouse_id " +
        "JOIN stores s ON s.id = w.store_id " +
        "JOIN product_version pv ON pv.id = i.product_version_id " +
        "JOIN product p ON p.id = pv.product_id AND p.deleted_at IS NULL " +
        "WHERE i.deleted_at IS NULL " +
        "AND (:storeId IS NULL OR s.id = :storeId) " +
        "AND (:warehouseId IS NULL OR w.id = :warehouseId) " +
        "AND (:productName IS NULL OR p.name ILIKE CONCAT('%', :productName, '%')) " +
        "AND i.created_at >= :startDate " +
        "AND i.created_at < :endDate " +
        "ORDER BY p.name, pv.version_number, i.created_at",
        nativeQuery = true)
    List<Inventory> findInventoryForReportAll(
        @Param("storeId") UUID storeId,
        @Param("warehouseId") UUID warehouseId,
        @Param("productName") String productName,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );

    // Debug method to check if there are any inventory records
    @Query(value = "SELECT COUNT(*) FROM inventory i WHERE i.deleted_at IS NULL", nativeQuery = true)
    long countAllActiveInventory();

    // Debug method to find inventory records without date filtering
    @Query(value = "SELECT i.* FROM inventory i " +
        "JOIN warehouses w ON w.id = i.warehouse_id " +
        "JOIN stores s ON s.id = w.store_id " +
        "JOIN product_version pv ON pv.id = i.product_version_id " +
        "JOIN product p ON p.id = pv.product_id AND p.deleted_at IS NULL " +
        "WHERE i.deleted_at IS NULL " +
        "AND (:storeId IS NULL OR s.id = :storeId) " +
        "AND (:warehouseId IS NULL OR w.id = :warehouseId) " +
        "AND (:productName IS NULL OR p.name ILIKE CONCAT('%', :productName, '%')) " +
        "ORDER BY p.name, pv.version_number, i.created_at",
        nativeQuery = true)
    List<Inventory> findInventoryWithoutDateFilter(
        @Param("storeId") UUID storeId,
        @Param("warehouseId") UUID warehouseId,
        @Param("productName") String productName
    );
}
