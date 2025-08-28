package com.pwdk.grocereach.promotion.infrastructure.repositories.impl;

import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductPromotions;
import com.pwdk.grocereach.product.infrastructures.repositories.impl.ProductPromotionRepoImpl;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.infrastructure.repositories.PromotionRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class PromotionRepoImpl {

  private final PromotionRepository promotionRepository;
  private final ProductPromotionRepoImpl productPromotionRepoImpl;

  public PromotionRepoImpl(PromotionRepository promotionRepository, ProductPromotionRepoImpl productPromotionRepoImpl) {
    this.promotionRepository = promotionRepository;
    this.productPromotionRepoImpl = productPromotionRepoImpl;
  }



}
