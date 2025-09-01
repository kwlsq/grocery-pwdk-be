package com.pwdk.grocereach.promotion.presentation.dto;

import com.pwdk.grocereach.promotion.domain.enums.PromotionType;
import com.pwdk.grocereach.promotion.domain.enums.PromotionUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromotionRequest {
  private String name;
  private String description;
  private BigDecimal value;
  private BigDecimal minPurchase;
  private String startAt;
  private String endAt;
  private PromotionType type;
  private PromotionUnit unit;
}
