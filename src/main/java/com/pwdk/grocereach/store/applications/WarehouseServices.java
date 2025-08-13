package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.presentations.dtos.WarehouseResponse;

import java.util.List;
import java.util.UUID;

public interface WarehouseServices {
  List<WarehouseResponse> getAllOwnedWarehouse(UUID storeID);
}
