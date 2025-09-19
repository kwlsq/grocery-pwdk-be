package com.pwdk.grocereach.store.presentations.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseRequest {
  @NotNull
  private String storeID;
  @NotNull
  private String name;
  @NotNull
  private String address;
  @NotNull
  private double latitude;
  @NotNull
  private double longitude;
  @NotNull
  private boolean isActive;
}
