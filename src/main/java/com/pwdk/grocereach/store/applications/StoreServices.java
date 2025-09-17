package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreServices {
  PaginatedResponse<StoreResponse> getAllStores(Pageable pageable, String search);
  StoreResponse getStoreByUser(String uuid);
}
