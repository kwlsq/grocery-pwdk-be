package com.pwdk.grocereach.product.infrastructures.repositories.impl;

import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductVersionRepository;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ProductVersionRepoImpl {

  private final ProductVersionRepository productVersionRepository;
  private final ProductRepository productRepository;
  private final ProductRepoImpl productRepoImpl;

  public ProductVersionRepoImpl(ProductVersionRepository productVersionRepository, ProductRepository productRepository, ProductRepoImpl productRepoImpl) {
    this.productVersionRepository = productVersionRepository;
    this.productRepository = productRepository;
    this.productRepoImpl = productRepoImpl;
  }

  public ProductVersions findVersionByID(UUID id) {
    return productVersionRepository.findById(id).orElseThrow(() -> (
        new RuntimeException("Version not found")
        ));
  }

  public ProductVersions createNewVersion(CreateProductRequest request, Product product) {
    ProductVersions version = buildNewProductVersion(request, product);
    setProductVersion(product, version);
    return saveProductVersion(version);
  }

  public ProductVersions buildNewProductVersion(CreateProductRequest request, Product product) {
    return ProductVersions.builder()
        .product(product)
        .price(request.getPrice())
        .weight(request.getWeight())
        .versionNumber(1) // set to become first version
        .changeReason("New product") // for creating new product
        .effectiveFrom(Instant.now())
        .build();
  }

  public void setProductVersion(Product product, ProductVersions version) {
    product.setCurrentVersion(version);
    productRepoImpl.saveProduct(product);
  }

  public ProductVersions saveProductVersion(ProductVersions version) {
    try {
      return productVersionRepository.save(version);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save product version: " + e.getMessage());
    }
  }

}
