package com.pwdk.grocereach.product.infrastructures.repositories;

import com.pwdk.grocereach.product.domains.entities.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
  Optional<Product> findByName(@NotNull String name);
  List<Product> findAllByStore_Id(UUID storeId);
  @Query("SELECT p FROM Product p " +
      "JOIN FETCH p.currentVersion cv " +
      "LEFT JOIN FETCH cv.inventories i " +
      "LEFT JOIN FETCH i.warehouse w " +
      "WHERE p.id = :productId " +
      "AND p.deletedAt IS NULL " +
      "AND cv.deletedAt IS NULL")
  Product findProductByIDWithInventories(@Param("productId") UUID productId);
}
