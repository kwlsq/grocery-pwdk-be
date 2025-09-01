package com.pwdk.grocereach.promotion.application.impl;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.common.exception.ProductNotFoundException;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductPromotions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductPromotionRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.promotion.application.PromotionService;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.infrastructure.repositories.PromotionRepository;
import com.pwdk.grocereach.promotion.presentation.dto.CreatePromotionRequest;
import com.pwdk.grocereach.promotion.presentation.dto.PromotionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PromotionServiceImplementation implements PromotionService {

  private final PromotionRepository promotionRepository;
  private final ProductRepository productRepository;
  private final ProductPromotionRepository productPromotionRepository;

  public PromotionServiceImplementation(PromotionRepository promotionRepository, ProductRepository productRepository, ProductPromotionRepository productPromotionRepository) {
    this.promotionRepository = promotionRepository;
    this.productRepository = productRepository;
    this.productPromotionRepository = productPromotionRepository;
  }

  @Override
  public PromotionResponse createPromotion(CreatePromotionRequest request) {

    Instant startDate = Instant.parse(request.getStartAt());
    Instant endDate = Instant.parse(request.getEndAt());

    Promotions promotion = new Promotions();
    promotion.setName(request.getName());
    promotion.setDescription(request.getDescription());
    promotion.setType(request.getType());
    promotion.setValue(request.getValue());
    promotion.setUnit(request.getUnit());
    promotion.setMinPurchase(request.getMinPurchase());
    promotion.setStartAt(startDate);
    promotion.setEndAt(endDate);
    promotion.setActive(true);

    promotionRepository.save(promotion);

    return PromotionResponse.from(promotion);
  }

  @Override
  public PaginatedResponse<PromotionResponse> getAllPromotions(Pageable pageable) {
    Page<Promotions> promotions = promotionRepository.findAll(pageable).map(promotion -> promotion);

    List<PromotionResponse> promotionResponses = promotions.getContent().stream()
        .map(PromotionResponse::from)
        .toList();

    return PaginatedResponse.Utils.from(promotions, promotionResponses);
  }
}
