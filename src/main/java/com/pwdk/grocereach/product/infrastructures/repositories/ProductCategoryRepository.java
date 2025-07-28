package com.pwdk.grocereach.product.infrastructures.repositories;

import com.pwdk.grocereach.product.domains.entities.ProductCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategories, UUID> {
}
