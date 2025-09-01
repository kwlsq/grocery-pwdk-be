package com.pwdk.grocereach.store.presentations.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseRequest {
  private String storeID;
  private String name;
  private String address;
  private double latitude;
  private double longitude;
  private boolean isActive;
}
