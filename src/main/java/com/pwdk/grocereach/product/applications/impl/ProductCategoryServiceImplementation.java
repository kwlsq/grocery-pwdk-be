package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.common.exception.CategoryNotFoundException;
import com.pwdk.grocereach.product.applications.ProductCategoryService;
import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductCategoryRepository;
import com.pwdk.grocereach.product.presentations.dtos.CreateCategoryRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductCategoryServiceImplementation implements ProductCategoryService {

  private final ProductCategoryRepository productCategoryRepository;

  public ProductCategoryServiceImplementation(ProductCategoryRepository productCategoryRepository) {
    this.productCategoryRepository = productCategoryRepository;
  }

  @Override
  public ProductCategory createCategory(CreateCategoryRequest request) {

    ProductCategory currentCategory = productCategoryRepository.findByName(request.getName());

    if (currentCategory != null) {
      throw new RuntimeException("Category with the same name already exist!");
    }

    ProductCategory parentCategory = null;

    if (request.getParentID() != null) {
      UUID parentID = UUID.fromString(request.getParentID());
      parentCategory = productCategoryRepository.findById(parentID).orElseThrow(() -> new CategoryNotFoundException("Category not found!"));
    }

    ProductCategory newCategory = new ProductCategory();
    newCategory.setName(request.getName());
    newCategory.setParent(parentCategory);

    return productCategoryRepository.save(newCategory);
  }
}
