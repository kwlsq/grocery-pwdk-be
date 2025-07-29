package com.pwdk.grocereach.product.infrastructures.repositories;

import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductVersionRepository extends JpaRepository<ProductVersions, UUID> {
}
