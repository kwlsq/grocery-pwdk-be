package com.pwdk.grocereach.store.presentations.dtos;

import com.pwdk.grocereach.store.domains.entities.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
  private UUID id;
  private String name;
  private String address;
  private double latitude;
  private double longitude;
  private boolean isActive;

  public static WarehouseResponse from(Warehouse warehouse) {
    return new WarehouseResponse(
        warehouse.getId(),
        warehouse.getName(),
        warehouse.getAddress(),
        warehouse.getLatitude(),
        warehouse.getLongitude(),
        warehouse.isActive()
    );
  }
}
