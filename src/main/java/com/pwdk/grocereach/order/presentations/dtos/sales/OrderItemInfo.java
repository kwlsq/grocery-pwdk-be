package com.pwdk.grocereach.order.presentations.dtos.sales;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemInfo {
  private UUID productId;
  private String productName;
  private UUID categoryId;
  private String categoryName;
  private Integer quantity;
  private BigDecimal price;
  private BigDecimal revenue;
}



