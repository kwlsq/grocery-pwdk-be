package com.pwdk.grocereach.product.infrastructures.repositories;

import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
  ProductCategory findByName(@NotNull String name);
}
