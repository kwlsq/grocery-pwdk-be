package com.pwdk.grocereach.product.infrastructures.repositories;

import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.presentations.dtos.UniqueProduct;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
  Optional<Product> findByName(@NotNull String name);
  @Query("SELECT DISTINCT p.id as id, p.name as name FROM Product p ")
  List<UniqueProduct> findAllUniqueProduct();
}
