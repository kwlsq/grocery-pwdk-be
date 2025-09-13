package com.pwdk.grocereach.order.applications;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import com.pwdk.grocereach.order.presentations.dtos.sales.MonthlyOrder;
import org.springframework.data.domain.Pageable;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.order.presentations.dtos.sales.OrderHistoryReportRow;

public interface SalesReportService {
  PaginatedResponse<OrderHistoryReportRow> getOrderHistoryReport(UUID storeId, UUID categoryId, UUID productId, YearMonth startMonth, YearMonth endMonth, Pageable pageable);
  List<MonthlyOrder> getMonthlyCount();
}


