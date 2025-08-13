package com.pwdk.grocereach.store.applications.implementation;

import com.pwdk.grocereach.store.applications.WarehouseServices;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.infrastructures.repositories.WarehouseRepository;
import com.pwdk.grocereach.store.presentations.dtos.WarehouseResponse;
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
  public List<WarehouseResponse> getAllOwnedWarehouse(UUID storeID) {
    return warehouseRepository.findAllByStore_Id(storeID).stream()
        .map(WarehouseResponse::from)
        .toList();
  }
}
