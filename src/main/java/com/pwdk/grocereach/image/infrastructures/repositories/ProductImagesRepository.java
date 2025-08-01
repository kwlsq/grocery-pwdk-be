package com.pwdk.grocereach.image.infrastructures.repositories;

import com.pwdk.grocereach.image.domains.entities.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, UUID> {
}
