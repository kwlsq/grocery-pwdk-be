package com.pwdk.grocereach.product.applications.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.impl.InventoryRepoImpl;
import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductRepoImpl;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductVersionRepoImpl;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.WarehouseRepoImpl;

@Component
public class ProductStockService {

  private final ProductRepoImpl productRepoImpl;
  private final ProductVersionRepoImpl productVersionRepoImpl;
  private final InventoryRepoImpl inventoryRepoImpl;
  private final WarehouseRepoImpl warehouseRepoImpl;

  public ProductStockService(ProductRepoImpl productRepoImpl,
                             ProductVersionRepoImpl productVersionRepoImpl,
                             InventoryRepoImpl inventoryRepoImpl,
                             WarehouseRepoImpl warehouseRepoImpl) {
    this.productRepoImpl = productRepoImpl;
    this.productVersionRepoImpl = productVersionRepoImpl;
    this.inventoryRepoImpl = inventoryRepoImpl;
    this.warehouseRepoImpl = warehouseRepoImpl;
  }

  public ProductResponse updateProductStock(UUID productID, List<WarehouseStock> warehouseStocks) {
    Product product = productRepoImpl.findProductByID(productID);
    ProductVersions version = product.getCurrentVersion();

    Map<UUID, Inventory> inventoryMap = new HashMap<>();
    if (version.getInventories() != null) {
      version.getInventories().forEach(inv -> inventoryMap.put(inv.getWarehouse().getId(), inv));
    }

    for (WarehouseStock warehouseStock : warehouseStocks) {
      UUID warehouseID = UUID.fromString(warehouseStock.getWarehouseID());
      Warehouse warehouse = warehouseRepoImpl.findWarehouseByID(warehouseStock.getWarehouseID());

      Inventory newInventory;
      if (!inventoryMap.containsKey(warehouseID)) {
        newInventory = inventoryRepoImpl.buildNewInventory(warehouse, warehouseStock.getStock(), version);
        inventoryMap.put(warehouseID, newInventory);
        inventoryRepoImpl.saveInventory(newInventory);
      } else {
        Inventory existingInventory = inventoryMap.get(warehouseID);
        Integer oldStock = existingInventory.getStock();
        Integer newStock = warehouseStock.getStock();

        if (!oldStock.equals(newStock)) {
          existingInventory.setDeletedAt(Instant.now());
          inventoryRepoImpl.saveInventory(existingInventory);

          int difference = newStock - oldStock;
          String journal = difference > 0 ? "+" + difference : "" + difference;

          newInventory = inventoryRepoImpl.buildNewInventory(warehouse, newStock, version, journal);
          inventoryMap.replace(warehouseID, newInventory);
          inventoryRepoImpl.saveInventory(newInventory);
        }
      }
    }

    List<Inventory> latestInventories = new ArrayList<>(inventoryMap.values());
    version.setInventories(latestInventories);
    productVersionRepoImpl.saveProductVersion(version);

    ProductVersions currentVersion = productVersionRepoImpl.findVersionByID(version.getId());
    product.setCurrentVersion(currentVersion);
    productRepoImpl.saveProduct(product);

    return ProductResponse.from(product);
  }
}



