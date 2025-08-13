package com.pwdk.grocereach.store.presentations.dtos;

import com.pwdk.grocereach.store.domains.entities.Stores;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {
  private UUID id;
  private String name;
  private String description;
  private String address;
  private boolean isActive;

  public static StoreResponse from(Stores stores) {
    return new StoreResponse(
        stores.getId(),
        stores.getStoreName(),
        stores.getDescription(),
        stores.getAddress(),
        stores.isActive()
    );
  }
}
