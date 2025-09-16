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

    List<Inventory> latestInventories = new ArrayList<>();

    for (WarehouseStock warehouseStock : warehouseStocks) {
      UUID warehouseID = UUID.fromString(warehouseStock.getWarehouseID());
      Warehouse warehouse = warehouseRepoImpl.findWarehouseByID(warehouseStock.getWarehouseID());
      Integer newStock = warehouseStock.getStock();

      // fetch the latest active inventory row for this warehouse + version
      Inventory latestInventory = inventoryRepoImpl
          .findTopByWarehouse_IdAndProductVersion_IdAndDeletedAtIsNullOrderByCreatedAtDesc(
              warehouseID, version.getId());

      int currentStock = (latestInventory != null && latestInventory.getStock() != null)
          ? latestInventory.getStock()
          : 0;

      // if no change, skip
      if (currentStock == newStock) {
        if (latestInventory != null) {
          latestInventories.add(latestInventory);
        }
        continue;
      }

      // soft delete previous snapshot
      if (latestInventory != null && latestInventory.getDeletedAt() == null) {
        latestInventory.setDeletedAt(Instant.now());
        inventoryRepoImpl.saveInventory(latestInventory);
      }

      int diff = newStock - currentStock;
      String journal = diff > 0 ? "+" + diff : String.valueOf(diff);

      Inventory newInventory = inventoryRepoImpl.buildNewInventory(warehouse, newStock, version, journal);
      inventoryRepoImpl.saveInventory(newInventory);
      latestInventories.add(newInventory);
    }

    version.setInventories(latestInventories);
    productVersionRepoImpl.saveProductVersion(version);

    ProductVersions currentVersion = productVersionRepoImpl.findVersionByID(version.getId());
    product.setCurrentVersion(currentVersion);
    productRepoImpl.saveProduct(product);

    return ProductResponse.from(product);
  }
}




