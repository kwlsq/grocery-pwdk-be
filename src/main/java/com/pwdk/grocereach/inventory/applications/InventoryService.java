package com.pwdk.grocereach.inventory.applications;

import com.pwdk.grocereach.inventory.presentations.dtos.InventoryResponse;

import java.util.UUID;

public interface InventoryService {
  InventoryResponse createStockInventory(UUID warehouseID, UUID productID);
}
