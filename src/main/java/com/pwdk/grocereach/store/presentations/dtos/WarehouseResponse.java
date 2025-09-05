package com.pwdk.grocereach.store.presentations.dtos;

import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
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
  private WarehouseAdmin warehouseAdmin;

  public static WarehouseResponse from(Warehouse warehouse) {
    return new WarehouseResponse(
        warehouse.getId(),
        warehouse.getName(),
        warehouse.getAddress(),
        warehouse.getLatitude(),
        warehouse.getLongitude(),
        warehouse.isActive(),
        warehouse.getUser() != null
            ? new WarehouseAdmin(
            warehouse.getUser().getId(),
            warehouse.getUser().getFullName(),
            warehouse.getUser().getRole(),
            warehouse.getUser().getPhotoUrl(),
            warehouse.getUser().getPhoneNumber()
        )
            : null
    );
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WarehouseAdmin {
    private UUID userID;
    private String userName;
    private UserRole userRole;
    private String photo;
    private String phoneNumber;
  }
}
