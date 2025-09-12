package com.pwdk.grocereach.order.presentations.dtos.sales;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderHistoryReportRow {
  private Instant updatedAt;
  private UUID orderId;
  private String status;
  private UUID storeId;
  private String storeName;
  private BigDecimal totalRevenue;
  private List<OrderItemInfo> items;
}
