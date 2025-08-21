package com.pwdk.grocereach.store.applications.implementation;

import com.pwdk.grocereach.store.applications.StoreServices;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.StoresRepository;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImplementation implements StoreServices {

  private final StoresRepository storesRepository;

  public StoreServiceImplementation(StoresRepository storesRepository) {
    this.storesRepository = storesRepository;
  }

  @Override
  public List<StoreResponse> getAllStores() {
    return storesRepository.findAll().stream()
        .map(StoreResponse::from)
        .toList();
  }
}
