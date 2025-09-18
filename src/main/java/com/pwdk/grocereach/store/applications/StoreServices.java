package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.store.presentations.dtos.StoreRequest;
import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import com.pwdk.grocereach.store.presentations.dtos.UniqueStore;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface StoreServices {
  StoreResponse createStore(StoreRequest request);
  StoreResponse getStoreById(UUID id);
  StoreResponse updateStore(UUID id, StoreRequest request);
  void deleteStore(UUID id);
  StoreResponse assignManagerToStore(UUID storeId, UUID userId);
  PaginatedResponse<StoreResponse> getAllStores(Pageable pageable, String search);
  StoreResponse getStoreByUser(String uuid);
  List<UniqueStore> getAllUniqueStore();
}
