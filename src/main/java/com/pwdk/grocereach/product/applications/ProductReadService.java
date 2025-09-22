package com.pwdk.grocereach.product.applications;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UniqueProduct;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductReadService {
  PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable, String search, String category, double userLatitude, double userLongitude, double maxDistanceKM);
  ProductResponse getProductByID(UUID id);
  List<UniqueProduct> getAllUniqueProduct();
  PaginatedResponse<ProductResponse> getProductsByStoreID(UUID storeID, Pageable pageable, String search, String category);
}
