package com.pwdk.grocereach.store.applications.implementation;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.infrastructures.specification.ProductSpecification;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.store.applications.WarehouseServices;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.infrastructures.repositories.WarehouseRepository;
import com.pwdk.grocereach.store.presentations.dtos.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WarehouseServiceImplementation implements WarehouseServices {

  private final WarehouseRepository warehouseRepository;

  public WarehouseServiceImplementation(WarehouseRepository warehouseRepository) {
    this.warehouseRepository = warehouseRepository;
  }

  @Override
  public PaginatedResponse<WarehouseResponse> getAllOwnedWarehouse(UUID storeID, Pageable pageable) {

    // Fetch warehouses (assuming correct entity type)
    Page<Warehouse> page = warehouseRepository.findAllByStore_Id(storeID, pageable);

    // Convert to response DTOs and filter empty inventories
    List<WarehouseResponse> filteredResponses = page.getContent().stream()
        .map(WarehouseResponse::from)
        .toList();

    // Return paginated response (metadata based on original page)
    return PaginatedResponse.Utils.from(page, filteredResponses);
  }
}
