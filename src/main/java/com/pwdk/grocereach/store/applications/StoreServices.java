package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreServices {
  List<StoreResponse> getAllStores();
  StoreResponse getStoreByUser(String uuid);
}
