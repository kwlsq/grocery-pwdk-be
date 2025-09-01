package com.pwdk.grocereach.inventory.infrastructures.repositories.impl;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.WarehouseRepoImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryRepoImpl {
  private final InventoryRepository inventoryRepository;
  private final WarehouseRepoImpl warehouseRepoImpl;

  public InventoryRepoImpl(InventoryRepository inventoryRepository,
                           WarehouseRepoImpl warehouseRepoImpl) {
    this.inventoryRepository = inventoryRepository;
    this.warehouseRepoImpl = warehouseRepoImpl;
  }

  public List<Inventory> createProductInventory (List<WarehouseStock> inventoryRequest, ProductVersions version, Product product) {
    List<Inventory> inventories = new ArrayList<>();

    inventoryRequest.forEach(inventoryReq -> {
      Warehouse warehouse = warehouseRepoImpl.findWarehouseByID(inventoryReq.getWarehouseID());
      Inventory inventory = buildNewInventory(warehouse, inventoryReq.getStock(), version);
      inventories.add(inventory);
      saveInventory(inventory);
    });

    return inventories;
  }

  public Inventory buildNewInventory(Warehouse warehouse, Integer stock, ProductVersions version) {
    return Inventory.builder()
        .warehouse(warehouse)
        .productVersion(version)
        .stock(stock)
        .journal("+" + stock)
        .build();
  }

  public Inventory saveInventory(Inventory inventory) {
    try {
      return inventoryRepository.save(inventory);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save inventory: " + e.getMessage());
    }
  }
}
