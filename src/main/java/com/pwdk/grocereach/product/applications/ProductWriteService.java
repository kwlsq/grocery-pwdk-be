package com.pwdk.grocereach.product.applications;

import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductWriteService {
  ProductResponse createProduct(CreateProductRequest request);
  ProductResponse updateProduct(UUID id, UpdateProductRequest request);
  void deleteProduct(UUID id);
  ProductResponse updateProductStock(UUID productID, List<WarehouseStock> warehouseStocks);
}
