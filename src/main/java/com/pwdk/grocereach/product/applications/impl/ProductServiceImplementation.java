package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.common.exception.MissingParameterException;
import com.pwdk.grocereach.common.exception.ProductAlreadyExistException;
import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.product.applications.ProductService;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductCategoryRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductVersionRepository;
import com.pwdk.grocereach.product.infrastructures.specification.ProductSpecification;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductCategoryResponse;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImplementation implements ProductService {

  private final ProductRepository productRepository;
  private final ProductVersionRepository productVersionRepository;
  private final ProductCategoryRepository productCategoryRepository;

  public ProductServiceImplementation (ProductRepository productRepository, ProductVersionRepository productVersionRepository, ProductCategoryRepository productCategoryRepository) {
    this.productRepository = productRepository;
    this.productVersionRepository = productVersionRepository;
    this.productCategoryRepository = productCategoryRepository;
  }

  @Override
  public ProductResponse createProduct(CreateProductRequest request) {

    UUID categoryUUID = UUID.fromString(request.getCategoryID()); // get UUID from categoryID string

//    Find the category
    ProductCategory category = productCategoryRepository.findById(categoryUUID).orElseThrow(() -> new ProductNotFoundException("Category not found!"));

//    Find existing product by name
    Optional<Product> productOptional = productRepository.findByName(request.getName());

    if (productOptional.isPresent()) {
      throw new ProductAlreadyExistException("Product with the same name already exist!");
    }

//    Create product first & save to DB *without product version
    Product product = Product.builder()
        .name(request.getName())
        .description(request.getDescription())
        .isActive(true)
        .category(category)
        .build();
    productRepository.save(product);

//    Create the product version & save it to DB
    ProductVersions versions = ProductVersions.builder()
        .product(product)
        .price(request.getPrice())
        .weight(request.getWeight())
        .versionNumber(1) // set to become first version
        .changeReason("New product") // for creating new product
        .effectiveFrom(Instant.now())
        .build();
    productVersionRepository.save(versions);

    product.setCurrentVersion(versions); //set the version to product
    productRepository.save(product);

    return ProductResponse.from(product);
  }

  @Override
  public PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable, String search, String category, double userLatitude, double userLongitude, double maxDistanceKM) {

    UUID categoryID = null;

    if (category != null && !category.trim().isEmpty()) {
      categoryID = UUID.fromString(category);
    }

    if (userLatitude == 0 || userLongitude == 0) {
      throw new MissingParameterException("User geolocation is required!");
    }

    Page<Product> page = productRepository.findAll(ProductSpecification.getFilteredProduct(search,categoryID), pageable).map(product -> product);

    List<ProductResponse> filteredResponses = page.getContent().stream()
        .map(product -> {
          ProductResponse response = ProductResponse.from(product);

          // Filter inventories by distance
          var filteredInventories = response.getProductVersionResponse().getInventories().stream()
              .filter(inv -> {
                double dist = haversine(
                    userLatitude,
                    userLongitude,
                    inv.getWarehouseLatitude(),
                    inv.getWarehouseLongitude()
                );
                return dist <= maxDistanceKM;
              })
              .toList();

          response.getProductVersionResponse().setInventories(filteredInventories);
          return response;
        })
        .filter(resp -> !resp.getProductVersionResponse().getInventories().isEmpty()) // remove products out of range
        .toList();

    return PaginatedResponse.Utils.from(page, filteredResponses);
  }

  @Override
  public ProductResponse getProductByID(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    return ProductResponse.from(product);
  }

  @Override
  public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
    Product currentProduct = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found!"));

    ProductVersions currentVersion = currentProduct.getCurrentVersion();
    System.out.println(request.getPrice());
    System.out.println(request.getStock());

    //    Create new product version
    ProductVersions newVersion = ProductVersions.builder()
        .product(currentProduct)
        .price(request.getPrice() != null ? request.getPrice() : currentVersion.getPrice())
        .weight(request.getWeight() != null ? request.getWeight() : currentVersion.getWeight())
        .versionNumber(currentProduct.getCurrentVersion().getVersionNumber() + 1)
        .changeReason(request.getChangeReason() != null ? request.getChangeReason() : "Changed by API endpoint!")
        .effectiveFrom(Instant.now())
        .build();
    productVersionRepository.save(newVersion);

//    Set effective to for unused version to now
    currentVersion.setEffectiveTo(Instant.now());

    currentProduct.setCurrentVersion(newVersion);
    currentProduct.setUpdatedAt(Instant.now());

//    If product's category is changed
    if (request.getCategoryID() != null) {
      UUID categoriUUID = UUID.fromString(request.getCategoryID());
      ProductCategory category = productCategoryRepository.findById(categoriUUID).orElseThrow(() -> new ProductNotFoundException("Category not found!"));

      currentProduct.setCategory(category);
    }

    productRepository.save(currentProduct); // Save updated product

    return ProductResponse.from(currentProduct);
  }

  @Override
  public void deleteProduct(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    product.setDeletedAt(Instant.now()); // Soft delete
    productRepository.save(product);
  }

  @Override
  public List<ProductCategoryResponse> getAllCategories() {
    return productCategoryRepository.findAll().stream()
        .map(ProductCategoryResponse::from)
        .toList();
  }

  private double haversine(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // km
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }
}
