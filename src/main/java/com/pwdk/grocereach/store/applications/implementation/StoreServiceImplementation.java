package com.pwdk.grocereach.store.applications.implementation;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.store.applications.StoreServices;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.StoresRepository;
import com.pwdk.grocereach.store.presentations.dtos.StoreRequest;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImplementation implements StoreServices {

  private final StoresRepository storeRepository;
  private final UserRepository userRepository;


  @Override
  public StoreResponse createStore(StoreRequest request) {
    Stores store = new Stores();
    store.setStoreName(request.getName());
    store.setDescription(request.getDescription());
    store.setAddress(request.getAddress());
    store.setLatitude(request.getLatitude());
    store.setLongitude(request.getLongitude());

    Stores savedStore = storeRepository.save(store);
    return new StoreResponse(savedStore);
  }

  @Override
  public List<StoreResponse> getAllStores() {
    return storeRepository.findAll().stream()
            .map(StoreResponse::new)
            .collect(Collectors.toList());
  }

  @Override
  public StoreResponse getStoreById(UUID id) {
    Stores store = storeRepository.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
    return new StoreResponse(store);
  }

  @Override
  public StoreResponse updateStore(UUID id, StoreRequest request) {
    Stores store = storeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Store not found with ID: " + id));
    store.setStoreName(request.getName());
    store.setDescription(request.getDescription());
    store.setAddress(request.getAddress());
    store.setLatitude(request.getLatitude());
    store.setLongitude(request.getLongitude());

    Stores updatedStore = storeRepository.save(store);
    return new StoreResponse(updatedStore);
  }

  @Override
  public void deleteStore(UUID id) {
    Stores store = storeRepository.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
    store.setDeletedAt(Instant.now());
    storeRepository.save(store);
  }

  @Override
  public StoreResponse assignManagerToStore(UUID storeId, UUID userId) {
    Stores store = storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Store not found"));
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole() != UserRole.MANAGER) {
      throw new IllegalStateException("Only users with the MANAGER role can be assigned to a store.");
    }

    store.setStoreManager(user);
    Stores updatedStore = storeRepository.save(store);
    return new StoreResponse(updatedStore);
  }
}
