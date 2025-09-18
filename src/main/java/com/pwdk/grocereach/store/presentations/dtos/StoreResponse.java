package com.pwdk.grocereach.store.presentations.dtos;

import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.store.domains.entities.Stores;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class StoreResponse {
  private UUID id;
  private String name;
  private String description;
  private String address;
  private Double latitude;
  private Double longitude;
  private boolean isActive;
  private UserResponse storeManager;

  public StoreResponse(Stores store) {
    this.id = store.getId();
    this.name = store.getStoreName();
    this.description = store.getDescription();
    this.address = store.getAddress();
    this.latitude = store.getLatitude();
    this.longitude = store.getLongitude();
    this.isActive = store.isActive();
    this.storeManager = (store.getStoreManager() != null)
            ? new UserResponse(store.getStoreManager())
            : null;}
}