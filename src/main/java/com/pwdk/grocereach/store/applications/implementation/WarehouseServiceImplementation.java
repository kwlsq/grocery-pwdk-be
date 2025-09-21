package com.pwdk.grocereach.store.applications.implementation;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.common.exception.StoreNotFoundException;
import com.pwdk.grocereach.store.applications.WarehouseServices;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.infrastructures.repositories.StoresRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.WarehouseRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.WarehouseRepoImpl;
import com.pwdk.grocereach.store.presentations.dtos.CreateWarehouseRequest;
import com.pwdk.grocereach.store.presentations.dtos.UniqueWarehouse;
import com.pwdk.grocereach.store.presentations.dtos.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WarehouseServiceImplementation implements WarehouseServices {

  private final WarehouseRepository warehouseRepository;
  private final StoresRepository storesRepository;
  private final WarehouseRepoImpl warehouseRepoImpl;

  public WarehouseServiceImplementation(WarehouseRepository warehouseRepository, StoresRepository storesRepository, WarehouseRepoImpl warehouseRepoImpl) {
    this.warehouseRepository = warehouseRepository;
    this.storesRepository = storesRepository;
    this.warehouseRepoImpl = warehouseRepoImpl;
  }

  @Override
  public PaginatedResponse<WarehouseResponse> getAllOwnedWarehouse(UUID storeID, Pageable pageable) {

    Page<Warehouse> page = warehouseRepository.findAllByStore_Id(storeID, pageable);

    List<WarehouseResponse> filteredResponses = page.getContent().stream()
        .map(WarehouseResponse::from)
        .toList();
    return PaginatedResponse.Utils.from(page, filteredResponses);
  }

  @Override
  public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {

    UUID storeID = UUID.fromString(request.getStoreID());

    Stores store = storesRepository.findById(storeID).orElseThrow(() -> new StoreNotFoundException("Store not found!"));

    Warehouse warehouse = new Warehouse();
    warehouse.setName(request.getName());
    warehouse.setAddress(request.getAddress());
    warehouse.setActive(true);
    warehouse.setLatitude(request.getLatitude());
    warehouse.setLongitude(request.getLongitude());
    warehouse.setStore(store);

    warehouseRepository.save(warehouse);

    return WarehouseResponse.from(warehouse);
  }

  @Override
  public WarehouseResponse getWarehouseByID(String id) {
    Warehouse warehouse = warehouseRepoImpl.findWarehouseByID(id);
    return WarehouseResponse.from(warehouse);
  }

  @Override
  public List<UniqueWarehouse> getAllUniqueWarehouse(String id) {
    UUID storeID = UUID.fromString(id);
    return warehouseRepository.findAllByStoreId(storeID);
  }
}
