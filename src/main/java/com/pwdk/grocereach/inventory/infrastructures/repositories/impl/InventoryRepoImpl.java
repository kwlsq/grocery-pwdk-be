package com.pwdk.grocereach.inventory.infrastructures.repositories.impl;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class InventoryRepoImpl {
  private final InventoryRepository inventoryRepository;

  public InventoryRepoImpl(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
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
