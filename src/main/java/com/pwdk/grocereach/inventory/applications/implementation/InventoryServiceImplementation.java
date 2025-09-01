package com.pwdk.grocereach.inventory.applications.implementation;

import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.inventory.applications.InventoryService;
import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
import com.pwdk.grocereach.inventory.presentations.dtos.InventoryResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductRepoImpl;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.infrastructures.repositories.WarehouseRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.WarehouseRepoImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryServiceImplementation implements InventoryService {

  private final ProductRepository productRepository;
  private final WarehouseRepository warehouseRepository;
  private final InventoryRepository inventoryRepository;
  private final ProductRepoImpl productRepoImpl;
  private final WarehouseRepoImpl warehouseRepoImpl;

  public InventoryServiceImplementation(ProductRepository productRepository, WarehouseRepository warehouseRepository, InventoryRepository inventoryRepository, ProductRepoImpl productRepoImpl, WarehouseRepoImpl warehouseRepoImpl) {
    this.productRepository = productRepository;
    this.warehouseRepository = warehouseRepository;
    this.inventoryRepository = inventoryRepository;
    this.productRepoImpl = productRepoImpl;
    this.warehouseRepoImpl = warehouseRepoImpl;
  }

  @Override
  public InventoryResponse createStockInventory(UUID warehouseID, UUID productID) {
    Product product = productRepository.findById(productID).orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    Warehouse warehouse = warehouseRepository.findById(warehouseID).orElseThrow(() -> new ProductNotFoundException("Warehouse not found!"));

    Inventory inventory = Inventory.builder()
        .productVersion(product.getCurrentVersion())
        .warehouse(warehouse)
        .build();

    inventoryRepository.save(inventory);

    return InventoryResponse.from(inventory);
  }
}
