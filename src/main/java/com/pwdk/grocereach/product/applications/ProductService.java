package com.pwdk.grocereach.product.applications;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
  ProductResponse createProduct(CreateProductRequest request);
  PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable, String search, Integer category, double userLatitude, double userLongitude, double maxDistanceKM);
  ProductResponse getProductByID(UUID id);
  ProductResponse updateProduct(UUID id, UpdateProductRequest request);
  void deleteProduct(UUID id);
}
