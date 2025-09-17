package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.product.presentations.dtos.UniqueProduct;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import com.pwdk.grocereach.store.presentations.dtos.UniqueStore;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreServices {
  PaginatedResponse<StoreResponse> getAllStores(Pageable pageable, String search);
  StoreResponse getStoreByUser(String uuid);
  List<UniqueStore> getAllUniqueStore();
}
