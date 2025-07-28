package com.pwdk.grocereach.product.infrastructures.repositories;

import com.pwdk.grocereach.product.domains.entities.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, UUID> {
}
