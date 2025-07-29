package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.product.applications.ProductService;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductVersionRepository;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImplementation implements ProductService {

  private final ProductRepository productRepository;
  private final ProductVersionRepository productVersionRepository;

  public ProductServiceImplementation (ProductRepository productRepository, ProductVersionRepository productVersionRepository) {
    this.productRepository = productRepository;
    this.productVersionRepository = productVersionRepository;
  }

  @Override
  public ProductResponse createProduct(CreateProductRequest request) {

//    Create product first & save to DB *without product version
    Product product = Product.builder()
        .name(request.getName())
        .description(request.getDescription())
        .isActive(true)
        .build();
    productRepository.save(product);

//    Create the product version & save it to DB
    ProductVersions versions = ProductVersions.builder()
        .product(product)
        .price(request.getPrice())
        .stock(request.getStock())
        .weight(request.getWeight())
        .effectiveFrom(Instant.now())
        .versionNumber(1)
        .build();
    productVersionRepository.save(versions);

    product.setCurrentVersion(versions); //set the version to product
    productRepository.save(product);

    return ProductResponse.from(product);
  }

  @Override
  public List<ProductResponse> getAllProducts() {
    return productRepository.findAll().stream()
        .map(ProductResponse::from)
        .toList();
  }

  @Override
  public ProductResponse getProductByID(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found!"));
    return ProductResponse.from(product);
  }

  @Override
  public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
    Product currentProduct = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found!"));

    //    Create new product version
    ProductVersions newVersion = ProductVersions.builder()
        .product(currentProduct)
        .price(request.getPrice())
        .stock(request.getStock())
        .weight(request.getWeight())
        .effectiveFrom(Instant.now())
        .versionNumber(currentProduct.getCurrentVersion().getVersionNumber() + 1)
        .build();
    productVersionRepository.save(newVersion);

    currentProduct.setCurrentVersion(newVersion);
    currentProduct.setUpdatedAt(Instant.now());

    productRepository.save(currentProduct); // Save updated product

    return ProductResponse.from(currentProduct);
  }

  @Override
  public void deleteProduct(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found!"));
    product.setDeletedAt(Instant.now());
    productRepository.save(product);
  }
}
