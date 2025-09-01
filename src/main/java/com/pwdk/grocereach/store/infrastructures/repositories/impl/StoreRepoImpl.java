package com.pwdk.grocereach.store.infrastructures.repositories.impl;

import com.pwdk.grocereach.common.exception.StoreNotFoundException;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.StoresRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StoreRepoImpl {
  private final StoresRepository storesRepository;

  public StoreRepoImpl(StoresRepository storesRepository) {
    this.storesRepository = storesRepository;
  }

  public Stores findStoreByID(String id) {
    UUID storeUUID = UUID.fromString(id);
    return storesRepository.findById(storeUUID).orElseThrow(() ->
        new StoreNotFoundException("Store not found!"));
  }
}
