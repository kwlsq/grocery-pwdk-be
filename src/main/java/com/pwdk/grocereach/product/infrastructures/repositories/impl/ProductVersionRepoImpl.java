package com.pwdk.grocereach.product.infrastructures.repositories.impl;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductVersionRepository;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ProductVersionRepoImpl {

  private final ProductVersionRepository productVersionRepository;
  private final ProductRepoImpl productRepoImpl;

  public ProductVersionRepoImpl(ProductVersionRepository productVersionRepository, ProductRepoImpl productRepoImpl) {
    this.productVersionRepository = productVersionRepository;
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

  public ProductVersions buildNewProductVersion(UpdateProductRequest request, Product product, List<Inventory> inventories, ProductVersions currentVersion) {
    return ProductVersions.builder()
        .product(product)
        .price(request.getPrice() != null ? request.getPrice() : currentVersion.getPrice())
        .weight(request.getWeight() != null ? request.getWeight() : currentVersion.getWeight())
        .versionNumber(product.getCurrentVersion().getVersionNumber() + 1)
        .changeReason("Update product by admin")
        .effectiveFrom(Instant.now())
        .inventories(inventories)
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
