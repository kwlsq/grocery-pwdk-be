package com.pwdk.grocereach.product.infrastructures.repositories.impl;

import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductCategoryRepoImpl {

  private final ProductCategoryRepository productCategoryRepository;

  public ProductCategoryRepoImpl(ProductCategoryRepository productCategoryRepository) {
    this.productCategoryRepository = productCategoryRepository;
  }

  public ProductCategory findCategoryByID(String id) {
    UUID categoryUUID = UUID.fromString(id);
    return productCategoryRepository.findById(categoryUUID).orElseThrow(() ->
        new ProductNotFoundException("Category not found!"));
  }

  public ProductCategory findCategoryByName(String name) {
    try {
      return productCategoryRepository.findByName(name);
    } catch (Exception e) {
      throw new ProductNotFoundException("Category not found!");
    }
  }
}
