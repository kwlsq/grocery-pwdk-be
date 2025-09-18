package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.common.exception.MissingParameterException;
import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.product.applications.ProductReadService;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.infrastructures.specification.ProductSpecification;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UniqueProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductReadServiceImplementation implements ProductReadService {

  private final ProductRepository productRepository;
  private final ProductDistanceFilterService productDistanceFilterService;

  public ProductReadServiceImplementation (ProductRepository productRepository, ProductDistanceFilterService productDistanceFilterService) {
    this.productRepository = productRepository;
    this.productDistanceFilterService = productDistanceFilterService;
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

    List<Product> allProducts = productRepository.findAll(ProductSpecification.searchByKeyword(search, categoryID, null)); // Get products with available inventory only (using existing specification)

    // Filter products to only include those with non-deleted inventory within range
    var filterResult = productDistanceFilterService.filterProductsByDistance(allProducts, userLatitude, userLongitude, maxDistanceKM);
    List<Product> filteredProducts = filterResult.products();
    List<ProductResponse> filteredResponses = filterResult.responses();

    // If no products found within range, fallback to HQ store products
    if (filteredProducts.isEmpty()) {
      UUID hqStoreId = UUID.fromString("288705db-7fff-48d1-b4dd-e0a87136bdc6");

      // Get products from HQ store
      List<Product> hqProducts = productRepository.findAll(ProductSpecification.searchByKeyword(search, categoryID, hqStoreId));

      // Filter HQ products with nearest warehouse inventory
      var hqFilterResult = productDistanceFilterService.filterProductsByDistance(hqProducts, userLatitude, userLongitude, Double.MAX_VALUE); // No distance limit for HQ
      filteredProducts = hqFilterResult.products();
      filteredResponses = hqFilterResult.responses();
    }

    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), filteredProducts.size());
    List<Product> paginatedProducts = start > filteredProducts.size() ? List.of() : filteredProducts.subList(start, end);
    List<ProductResponse> paginatedResponses = start > filteredResponses.size() ? List.of() : filteredResponses.subList(start, end);

    Page<Product> customPage = new org.springframework.data.domain.PageImpl<>(
        paginatedProducts,
        pageable,
        filteredProducts.size()
    );

    return PaginatedResponse.Utils.from(customPage, paginatedResponses);
  }

  @Override
  public ProductResponse getProductByID(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    return ProductResponse.from(product);
  }

  @Override
  public PaginatedResponse<ProductResponse> getProductsByStoreID(UUID storeID, Pageable pageable, String search, String category) {

    UUID categoryID = null;
    if (category != null && !category.trim().isEmpty()) {
      categoryID = UUID.fromString(category);
    }

    Page<Product> page = productRepository.findAll(
        ProductSpecification.getFilteredProduct(search, categoryID, storeID), pageable);

    List<ProductResponse> filteredResponses = page.getContent().stream()
        .map(product -> {
          ProductResponse response = ProductResponse.from(product);

          // ✅ filter inventories after mapping to DTO
          if (response.getProductVersionResponse() != null &&
              response.getProductVersionResponse().getInventories() != null) {
            response.getProductVersionResponse().setInventories(
                response.getProductVersionResponse().getInventories().stream()
                    .filter(inv -> inv.getDeletedAt() == null) // keep only active inventories
                    .toList()
            );
          }

          return response;
        })
        .toList();

    return PaginatedResponse.Utils.from(page, filteredResponses);
  }

  @Override
  public List<UniqueProduct> getAllUniqueProduct() {
    return productRepository.findAllUniqueProduct();
  }
}
