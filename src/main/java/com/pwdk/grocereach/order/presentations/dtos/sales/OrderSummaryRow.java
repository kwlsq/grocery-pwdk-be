package com.pwdk.grocereach.order.presentations.dtos.sales;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface OrderSummaryRow {
  Instant getUpdatedAt();
  UUID getOrderId();
  String getStatus();
  UUID getStoreId();
  String getStoreName();
  BigDecimal getTotalRevenue();
}



