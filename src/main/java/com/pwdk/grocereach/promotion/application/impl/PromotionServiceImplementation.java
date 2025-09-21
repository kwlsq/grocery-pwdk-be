package com.pwdk.grocereach.promotion.application.impl;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.promotion.application.PromotionService;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.infrastructure.repositories.PromotionRepository;
import com.pwdk.grocereach.promotion.infrastructure.specifications.PromotionSpecification;
import com.pwdk.grocereach.promotion.presentation.dto.CreatePromotionRequest;
import com.pwdk.grocereach.promotion.presentation.dto.PromotionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PromotionServiceImplementation implements PromotionService {

  private final PromotionRepository promotionRepository;

  public PromotionServiceImplementation(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  @Override
  public PromotionResponse createPromotion(CreatePromotionRequest request) {

    Instant startDate = Instant.parse(request.getStartAt());
    Instant endDate = Instant.parse(request.getEndAt());

    Promotions promotion = createPromotionObject(request, startDate, endDate);

    promotionRepository.save(promotion);

    return PromotionResponse.from(promotion);
  }

  @Override
  public PaginatedResponse<PromotionResponse> getAllPromotions(Pageable pageable, String search) {

    Page<Promotions> promotions = promotionRepository.findAll(PromotionSpecification.getFilteredStore(search), pageable);

    List<PromotionResponse> promotionResponses = promotions.getContent().stream()
        .map(PromotionResponse::from)
        .toList();

    return PaginatedResponse.Utils.from(promotions, promotionResponses);
  }

  public Promotions createPromotionObject(CreatePromotionRequest request, Instant startDate, Instant endDate) {
    return Promotions.builder()
        .name(request.getName())
        .description(request.getDescription())
        .type(request.getType())
        .value(request.getValue())
        .unit(request.getUnit())
        .minPurchase(request.getMinPurchase())
        .startAt(startDate)
        .endAt(endDate)
        .isActive(true)
        .build();
  }
}
