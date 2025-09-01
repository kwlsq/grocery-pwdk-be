package com.pwdk.grocereach.store.infrastructures.repositories.impl;

import com.pwdk.grocereach.common.exception.WarehouseNotFoundException;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.infrastructures.repositories.WarehouseRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WarehouseRepoImpl {

  private final WarehouseRepository warehouseRepository;

  public WarehouseRepoImpl(WarehouseRepository warehouseRepository) {
    this.warehouseRepository = warehouseRepository;
  }

  public Warehouse findWarehouseByID(String id) {
    UUID warehouseID = UUID.fromString(id);
    return warehouseRepository.findById(warehouseID)
        .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found!"));
  }
}
