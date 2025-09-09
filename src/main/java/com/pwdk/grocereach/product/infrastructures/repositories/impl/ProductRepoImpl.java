package com.pwdk.grocereach.product.infrastructures.repositories.impl;

import com.pwdk.grocereach.common.exception.ProductAlreadyExistException;
import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductCategoryRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.StoreRepoImpl;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepoImpl {

  private final ProductRepository productRepository;
  private final StoreRepoImpl storeRepoImpl;

  public ProductRepoImpl(ProductRepository productRepository, StoreRepoImpl storeRepoImpl) {
    this.productRepository = productRepository;
    this.storeRepoImpl = storeRepoImpl;
  }

  public Product createProduct(CreateProductRequest request, ProductCategory category, Stores store) {
    validateProductNameNotExists(request.getName(), request.getStoreID());
    Product product = buildNewProduct(request, category, store);
    return saveProduct(product);
  }

  public void validateProductNameNotExists(String name, String storeID) {
    Stores requestStore = storeRepoImpl.findStoreByID(storeID);
    Optional<Product> product = productRepository.findByName(name);
    if (product.isPresent()) {
      Stores store = product.get().getStore();
      if (requestStore.getId().equals(store.getId())) {
        throw new ProductAlreadyExistException("Product with name '" + name +"' already exists in store " + store.getStoreName() + " !");
      }
    }
  }

  public Product buildNewProduct(CreateProductRequest request, ProductCategory category, Stores store) {
    return Product.builder()
        .name(request.getName())
        .description(request.getDescription())
        .isActive(true)
        .category(category)
        .store(store)
        .build();
  }

  public Product findProductByID(UUID id) {
    return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found!"));
  }

  public Product saveProduct(Product product) {
    try {
      return productRepository.save(product);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save product: " + e.getMessage());
    }
  }


}
