package com.pwdk.grocereach.inventory.domains.interfaces;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryMonthlyReport {
  String getProductName();
  UUID getProductId();
  String getWarehouseName();
  UUID getWarehouseId();
  String getStoreName();
  String getMonth();
  Integer getLatestVersion();
  Integer getTotalAddition();
  Integer getTotalReduction();
  Integer getFinalStock();
  BigDecimal getAveragePrice();
}
