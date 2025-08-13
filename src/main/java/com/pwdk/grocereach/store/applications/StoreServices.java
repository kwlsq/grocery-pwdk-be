package com.pwdk.grocereach.store.applications;

import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;

import java.util.List;

public interface StoreServices {
  List<StoreResponse> getAllStores();
}
