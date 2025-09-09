package com.pwdk.grocereach.inventory.presentations.dtos;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
  private Integer stock;
  private UUID warehouseID;
  private double warehouseLatitude;
  private double warehouseLongitude;
  private Instant deletedAt;

  public static InventoryResponse from(Inventory inventory)  {
    return new InventoryResponse(
        inventory.getStock(),
        inventory.getWarehouse().getId(),
        inventory.getWarehouse().getLatitude(),
        inventory.getWarehouse().getLongitude(),
        inventory.getDeletedAt()
    );
  }
}
