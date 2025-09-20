package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.product.applications.ProductCategoryService;
import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductCategoryRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductCategoryRepoImpl;
import com.pwdk.grocereach.product.presentations.dtos.CreateCategoryRequest;
import com.pwdk.grocereach.product.presentations.dtos.ProductCategoryResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

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
    productCategoryRepoImpl.findCategoryByName(request.getName()); // check category with the same name

    ProductCategory parentCategory = null;

    if (request.getParentID() != null) {
      parentCategory = productCategoryRepoImpl.findCategoryByID(request.getParentID());
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
