package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.presentations.dtos.CreateWarehouseRequest;
import com.pwdk.grocereach.store.presentations.dtos.WarehouseResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface WarehouseServices {
  PaginatedResponse<WarehouseResponse> getAllOwnedWarehouse(UUID storeID, Pageable pageable);
  WarehouseResponse createWarehouse(CreateWarehouseRequest request);
  WarehouseResponse getWarehouseByID(String id);
  WarehouseResponse getWarehouseByUser(UUID userId);
}
