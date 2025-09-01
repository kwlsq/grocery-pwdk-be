package com.pwdk.grocereach.product.applications.impl;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.common.exception.MissingParameterException;
import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
import com.pwdk.grocereach.inventory.infrastructures.repositories.impl.InventoryRepoImpl;
import com.pwdk.grocereach.product.applications.ProductService;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.domains.entities.ProductPromotions;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductCategoryRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductPromotionRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductVersionRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductCategoryRepoImpl;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductPromotionRepoImpl;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductRepoImpl;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductVersionRepoImpl;
import com.pwdk.grocereach.product.infrastructures.specification.ProductSpecification;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductCategoryResponse;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.infrastructure.repositories.PromotionRepository;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.StoresRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.WarehouseRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.StoreRepoImpl;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.WarehouseRepoImpl;

@Service
public class ProductServiceImplementation implements ProductService {

  private final ProductRepository productRepository;
  private final ProductVersionRepository productVersionRepository;
  private final ProductCategoryRepository productCategoryRepository;
  private final WarehouseRepository warehouseRepository;
  private final StoresRepository storesRepository;
  private final InventoryRepository inventoryRepository;
  private final PromotionRepository promotionRepository;
  private final ProductPromotionRepository productPromotionRepository;
  private final ProductRepoImpl productRepoImpl;
  private final StoreRepoImpl storeRepoImpl;
  private final ProductCategoryRepoImpl productCategoryRepoImpl;
  private final ProductVersionRepoImpl productVersionRepoImpl;
  private final InventoryRepoImpl inventoryRepoImpl;
  private final WarehouseRepoImpl warehouseRepoImpl;
  private final ProductPromotionRepoImpl productPromotionRepoImpl;

  public ProductServiceImplementation (ProductRepository productRepository,
                                       ProductVersionRepository productVersionRepository,
                                       ProductCategoryRepository productCategoryRepository,
                                       WarehouseRepository warehouseRepository,
                                       StoresRepository storesRepository,
                                       InventoryRepository inventoryRepository,
                                       PromotionRepository promotionRepository,
                                       ProductPromotionRepository productPromotionRepository, ProductRepoImpl productRepoImpl, StoreRepoImpl storeRepoImpl, ProductCategoryRepoImpl productCategoryRepoImpl, ProductVersionRepoImpl productVersionRepoImpl, InventoryRepoImpl inventoryRepoImpl, WarehouseRepoImpl warehouseRepoImpl, ProductPromotionRepoImpl productPromotionRepoImpl) {
    this.productRepository = productRepository;
    this.productVersionRepository = productVersionRepository;
    this.productCategoryRepository = productCategoryRepository;
    this.warehouseRepository = warehouseRepository;
    this.storesRepository = storesRepository;
    this.inventoryRepository = inventoryRepository;
    this.productPromotionRepository = productPromotionRepository;
    this.promotionRepository = promotionRepository;
    this.productRepoImpl = productRepoImpl;
    this.storeRepoImpl = storeRepoImpl;
    this.productCategoryRepoImpl = productCategoryRepoImpl;
    this.productVersionRepoImpl = productVersionRepoImpl;
    this.inventoryRepoImpl = inventoryRepoImpl;
    this.warehouseRepoImpl = warehouseRepoImpl;
    this.productPromotionRepoImpl = productPromotionRepoImpl;
  }

  @Override
  @Transactional
  public ProductResponse createProduct(CreateProductRequest request) {
    ProductCategory category = productCategoryRepoImpl.findCategoryByID(request.getCategoryID());
    Stores store = storeRepoImpl.findStoreByID(request.getStoreID());
    Product product = productRepoImpl.createProduct(request, category, store);
    ProductVersions version = productVersionRepoImpl.createNewVersion(request, product);

    inventoryRepoImpl.createProductInventory(request.getInventories(), version, product); // Create and save inventories

    ProductVersions refreshedVersion = productVersionRepoImpl.findVersionByID(version.getId()); // Refresh the product version to get the latest state with inventories

    product.setCurrentVersion(refreshedVersion); // Set the refreshed version with inventories back to the product
    productRepository.save(product);

    productPromotionRepoImpl.createProductPromotions(request.getPromotions(), product);

    return ProductResponse.from(productRepoImpl.findProductByID(product.getId()));
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

    Page<Product> page = productRepository.findAll(ProductSpecification.searchByKeyword(search,categoryID,null), pageable).map(product -> product);

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

    Set<ProductPromotions> promotions = currentProduct.getProductPromotions();

    request.getPromotions().forEach((promotionRequest) -> {
      UUID promotionID = UUID.fromString(promotionRequest.getPromotionID());
      Promotions promotion = promotionRepository.findById(promotionID).orElseThrow(() -> new RuntimeException("Promotion not found!"));
      ProductPromotions productPromotion = new ProductPromotions();
      productPromotion.setPromotion(promotion);
      productPromotion.setProduct(currentProduct);
      promotions.add(productPromotion);
    });

    productPromotionRepository.saveAll(promotions);

    currentProduct.setProductPromotions(promotions);
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

  @Override
  public PaginatedResponse<ProductResponse> getProductsByStoreID(UUID storeID, Pageable pageable, String search, String category) {

    UUID categoryID = null;

    if (category != null && !category.trim().isEmpty()) {
      categoryID = UUID.fromString(category);
    }

    Page<Product> page = productRepository.findAll(ProductSpecification.getFilteredProduct(search,categoryID, storeID), pageable).map(product -> product);

    List<ProductResponse> filteredResponses = page.getContent().stream()
        .map(ProductResponse::from)
        .toList();

    return PaginatedResponse.Utils.from(page, filteredResponses);
  }

  @Override
  public ProductResponse updateProductStock(UUID productID, List<WarehouseStock> warehouseStocks) {
    Product product = productRepoImpl.findProductByID(productID);
    ProductVersions version = product.getCurrentVersion();

    List<Inventory> existingInventories = version.getInventories();
    Map<UUID, Inventory> existingInventoryMap = existingInventories.stream()
        .collect(Collectors.toMap(inv -> inv.getWarehouse().getId(), inv -> inv));

    for (WarehouseStock warehouseStock : warehouseStocks) {
      UUID warehouseID = UUID.fromString(warehouseStock.getWarehouseID());
      Warehouse warehouse = warehouseRepoImpl.findWarehouseByID(warehouseStock.getWarehouseID());

      Inventory inventory = existingInventoryMap.get(warehouseID);

      if (inventory != null) {
        // Update existing inventory
        Integer oldStock = inventory.getStock();
        Integer newStock = warehouseStock.getStock();
        Integer stockDifference = newStock - oldStock;

        if (!oldStock.equals(newStock)) {
          inventory.setStock(newStock);
          inventory.setJournal((stockDifference > 0 ? "+" : "-") + stockDifference);
        }
      } else {
        // Create new inventory if it doesn't exist
        inventory = new Inventory();
        inventory.setProductVersion(version);
        inventory.setWarehouse(warehouse);
        inventory.setStock(warehouseStock.getStock());
        inventory.setJournal("+");

        inventoryRepoImpl.saveInventory(inventory);
        existingInventories.add(inventory);
      }
    }

    version.setInventories(existingInventories);
    productVersionRepoImpl.saveProductVersion(version);

    return ProductResponse.from(product);
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
