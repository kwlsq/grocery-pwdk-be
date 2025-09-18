package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.presentations.dtos.StoreRequest;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;

import java.util.List;
import java.util.UUID;

public interface StoreServices {
  StoreResponse createStore(StoreRequest request);
  List<StoreResponse> getAllStores();
  StoreResponse getStoreById(UUID id);
  StoreResponse updateStore(UUID id, StoreRequest request);
  void deleteStore(UUID id);
  StoreResponse assignManagerToStore(UUID storeId, UUID userId);}
