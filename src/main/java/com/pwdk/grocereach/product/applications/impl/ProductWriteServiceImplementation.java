package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.impl.InventoryRepoImpl;
import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import com.pwdk.grocereach.product.applications.ProductWriteService;
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
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.infrastructure.repositories.PromotionRepository;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.StoreRepoImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductWriteServiceImplementation implements ProductWriteService {


  private final ProductRepository productRepository;
  private final ProductVersionRepository productVersionRepository;
  private final ProductCategoryRepository productCategoryRepository;
  private final PromotionRepository promotionRepository;
  private final ProductPromotionRepository productPromotionRepository;
  private final ProductRepoImpl productRepoImpl;
  private final StoreRepoImpl storeRepoImpl;
  private final ProductCategoryRepoImpl productCategoryRepoImpl;
  private final ProductVersionRepoImpl productVersionRepoImpl;
  private final InventoryRepoImpl inventoryRepoImpl;
  private final ProductPromotionRepoImpl productPromotionRepoImpl;
  private final ProductStockService productStockService;

  public ProductWriteServiceImplementation (ProductRepository productRepository,
                                       ProductVersionRepository productVersionRepository,
                                       ProductCategoryRepository productCategoryRepository,
                                       PromotionRepository promotionRepository,
                                       ProductPromotionRepository productPromotionRepository, ProductRepoImpl productRepoImpl, StoreRepoImpl storeRepoImpl, ProductCategoryRepoImpl productCategoryRepoImpl, ProductVersionRepoImpl productVersionRepoImpl, InventoryRepoImpl inventoryRepoImpl, ProductPromotionRepoImpl productPromotionRepoImpl, ProductStockService productStockService) {
    this.productRepository = productRepository;
    this.productVersionRepository = productVersionRepository;
    this.productCategoryRepository = productCategoryRepository;
    this.productPromotionRepository = productPromotionRepository;
    this.promotionRepository = promotionRepository;
    this.productRepoImpl = productRepoImpl;
    this.storeRepoImpl = storeRepoImpl;
    this.productCategoryRepoImpl = productCategoryRepoImpl;
    this.productVersionRepoImpl = productVersionRepoImpl;
    this.inventoryRepoImpl = inventoryRepoImpl;
    this.productPromotionRepoImpl = productPromotionRepoImpl;
    this.productStockService = productStockService;
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

    if (request.getPromotions() != null) {
      productPromotionRepoImpl.createProductPromotions(request.getPromotions(), product);
    }

    return ProductResponse.from(productRepoImpl.findProductByID(product.getId()));
  }

  @Override
  public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
    Product currentProduct = productRepoImpl.findProductByID(id);

    ProductVersions currentVersion = currentProduct.getCurrentVersion();

    currentProduct.setName(request.getName() != null ? request.getName() : currentProduct.getName());
    currentProduct.setDescription(request.getDescription() != null ? request.getDescription() : currentProduct.getDescription());

    List<Inventory> copiedInventories = null;
    if (currentVersion.getInventories() != null) {
      copiedInventories = currentVersion.getInventories().stream()
          .map(oldInventory -> Inventory.builder()
              .stock(oldInventory.getStock())
              .journal("Version migration: " + oldInventory.getJournal().replace("Version migration: ", ""))
              .warehouse(oldInventory.getWarehouse())
              .build())
          .collect(Collectors.toList());
    }

    ProductVersions newVersion = productVersionRepoImpl.buildNewProductVersion(request, currentProduct, copiedInventories, currentVersion); // Create new product version

// Set the product version reference for each copied inventory
    if (copiedInventories != null) {
      copiedInventories.forEach(inventory -> inventory.setProductVersion(newVersion));
    }

    productVersionRepository.save(newVersion);

    currentVersion.setEffectiveTo(Instant.now()); //    Set effective to for unused version to now

    if (request.getPromotions() != null) {
      Set<ProductPromotions> promotions = currentProduct.getProductPromotions();
      request.getPromotions().forEach((promotionRequest) -> {
        UUID promotionID = UUID.fromString(promotionRequest.getPromotionID());
        Promotions promotion = promotionRepository.findById(promotionID).orElseThrow(() -> new RuntimeException("Promotion not found!"));
        ProductPromotions productPromotion = productPromotionRepoImpl.buildNewProductPromotion(promotion, currentProduct);
        promotions.add(productPromotion);
      });

      productPromotionRepository.saveAll(promotions);
      currentProduct.setProductPromotions(promotions);
    }
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
  public ProductResponse updateProductStock(UUID productID, List<WarehouseStock> warehouseStocks) {
    return productStockService.updateProductStock(productID, warehouseStocks);
  }
}
