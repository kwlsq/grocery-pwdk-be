package com.pwdk.grocereach.product.applications;

import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {
  ProductResponse createProduct(CreateProductRequest request);
  List<ProductResponse> getAllProducts();
  ProductResponse getProductByID(UUID id);
  ProductResponse updateProduct(UUID id, UpdateProductRequest request);
  void deleteProduct(UUID id);
}
