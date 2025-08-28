package com.pwdk.grocereach.product.presentations.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
  private String name;
  private String description;
  private BigDecimal price;
  private BigDecimal weight;
  private String changeReason;
  private String categoryID;
  private List<AttachPromotion> promotions;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AttachPromotion {
    private String promotionID;
  }
}
