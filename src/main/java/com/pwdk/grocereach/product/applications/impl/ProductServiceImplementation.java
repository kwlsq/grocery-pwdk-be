package com.pwdk.grocereach.product.applications.impl;

import com.pwdk.grocereach.product.applications.ProductService;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImplementation implements ProductService {

  private final ProductRepository productRepository;

  public ProductServiceImplementation (ProductRepository productRepository) {
    this.productRepository = productRepository;
  }
}
