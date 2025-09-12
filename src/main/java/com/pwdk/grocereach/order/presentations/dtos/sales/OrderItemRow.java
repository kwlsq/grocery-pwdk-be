package com.pwdk.grocereach.order.presentations.dtos.sales;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderItemRow {
  UUID getOrderId();
  UUID getProductId();
  String getProductName();
  UUID getCategoryId();
  String getCategoryName();
  Integer getQuantity();
  BigDecimal getPrice();
  BigDecimal getRevenue();
}



