package com.pwdk.grocereach.product.applications;

import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.presentations.dtos.CreateCategoryRequest;

public interface ProductCategoryService {
  ProductCategory createCategory(CreateCategoryRequest request);
}
