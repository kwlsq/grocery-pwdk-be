package com.pwdk.grocereach.product.applications;

import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.presentations.dtos.CreateCategoryRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {
  List<ProductCategoryResponse> getAllCategories();
  ProductCategory createCategory(CreateCategoryRequest request);
  void deleteCategory (String id);
}
