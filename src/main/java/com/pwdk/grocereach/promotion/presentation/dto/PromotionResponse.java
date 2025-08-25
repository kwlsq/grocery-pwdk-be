package com.pwdk.grocereach.promotion.presentation.dto;

import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.domain.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal value;
  private BigDecimal minPurchase;
  private Instant startAt;
  private Instant endAt;
  private PromotionType type;

  public static PromotionResponse from(Promotions promotions) {
    return new PromotionResponse(
        promotions.getId(),
        promotions.getName(),
        promotions.getDescription(),
        promotions.getValue(),
        promotions.getMinPurchase(),
        promotions.getStartAt(),
        promotions.getEndAt(),
        promotions.getType()
    );
  }
}
