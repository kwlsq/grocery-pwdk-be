package com.pwdk.grocereach.product.infrastructures.repositories.impl;

import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductPromotions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductPromotionRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.infrastructure.repositories.PromotionRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class ProductPromotionRepoImpl {
  private final ProductPromotionRepository productPromotionRepository;
  private final PromotionRepository promotionRepository;
  private final ProductRepository productRepository;

  public ProductPromotionRepoImpl(ProductPromotionRepository productPromotionRepository, PromotionRepository promotionRepository, ProductRepository productRepository) {
    this.productPromotionRepository = productPromotionRepository;
    this.promotionRepository = promotionRepository;
    this.productRepository = productRepository;
  }

  public Set<ProductPromotions> createProductPromotions(List<UpdateProductRequest.AttachPromotion> request, Product product) {

    Set<ProductPromotions> promotions = new HashSet<>();

    request.forEach(promotionRequest -> {
      UUID promotionID = UUID.fromString(promotionRequest.getPromotionID());
      Promotions promotion = promotionRepository.findById(promotionID).orElseThrow(() -> new RuntimeException("Promotion not found!"));
      ProductPromotions productPromotion = buildNewProductPromotion(promotion, product);
      promotions.add(productPromotion);
      saveProductPromotion(productPromotion);
    });

    saveProductWithPromotion(product, promotions);

    return promotions;
  }

  public ProductPromotions buildNewProductPromotion (Promotions promotion, Product product) {
    ProductPromotions productPromotions =  ProductPromotions.builder()
        .promotion(promotion)
        .product(product)
        .build();

    productPromotionRepository.save(productPromotions);
    return productPromotions;
  }

  public void saveProductWithPromotion(Product product, Set<ProductPromotions> promotions) {
    product.setProductPromotions(promotions);
    productRepository.save(product);
  }

  public ProductPromotions saveProductPromotion(ProductPromotions productPromotion) {
    try {
      return productPromotionRepository.save(productPromotion);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save product promotion: " + e.getMessage());
    }
  }
}
