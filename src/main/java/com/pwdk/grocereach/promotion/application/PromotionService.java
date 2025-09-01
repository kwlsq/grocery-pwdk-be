package com.pwdk.grocereach.promotion.application;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.presentation.dto.CreatePromotionRequest;
import com.pwdk.grocereach.promotion.presentation.dto.PromotionResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PromotionService {
  PromotionResponse createPromotion(CreatePromotionRequest request);
  PaginatedResponse<PromotionResponse> getAllPromotions(Pageable pageable);
}
