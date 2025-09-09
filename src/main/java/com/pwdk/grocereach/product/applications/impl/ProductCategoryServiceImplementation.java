package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.common.exception.CategoryNotFoundException;
import com.pwdk.grocereach.product.applications.ProductCategoryService;
import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductCategoryRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductCategoryRepoImpl;
import com.pwdk.grocereach.product.presentations.dtos.CreateCategoryRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductCategoryResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProductCategoryServiceImplementation implements ProductCategoryService {

  private final ProductCategoryRepository productCategoryRepository;
  private final ProductCategoryRepoImpl productCategoryRepoImpl;

  public ProductCategoryServiceImplementation(ProductCategoryRepository productCategoryRepository, ProductCategoryRepoImpl productCategoryRepoImpl) {
    this.productCategoryRepository = productCategoryRepository;
    this.productCategoryRepoImpl = productCategoryRepoImpl;
  }

  @Override
  public ProductCategory createCategory(CreateCategoryRequest request) {

    ProductCategory currentCategory = productCategoryRepoImpl.findCategoryByName(request.getName());

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

  @Override
  public List<ProductCategoryResponse> getAllCategories() {
    return productCategoryRepository.findAll().stream()
        .map(ProductCategoryResponse::from)
        .toList();
  }

  @Override
  public void deleteCategory(String id) {
    ProductCategory category = productCategoryRepoImpl.findCategoryByID(id);

    category.setDeletedAt(Instant.now());

    productCategoryRepository.save(category);
  }
}
