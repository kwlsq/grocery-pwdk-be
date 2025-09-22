package com.pwdk.grocereach.order.applications.implementation;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pwdk.grocereach.order.presentations.dtos.sales.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.order.applications.SalesReportService;
import com.pwdk.grocereach.order.infrastructures.repositories.OrderHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesReportServiceImplementation implements SalesReportService {

  private final OrderHistoryRepository orderHistoryRepository;

  private Instant startOf(YearMonth ym) { return ym.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC); }
  private Instant endExclusive(YearMonth ym) { YearMonth next = ym.plusMonths(1); return next.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC); }

  @Override
  public PaginatedResponse<OrderHistoryReportRow> getOrderHistoryReport(UUID storeId, UUID categoryId, UUID productId, YearMonth startMonth, YearMonth endMonth, Pageable pageable) {
    Instant start = startOf(startMonth);
    Instant end = endExclusive(endMonth);
    Page<OrderSummaryRow> page = orderHistoryRepository.findOrderHistoryReport(storeId, categoryId, productId, start, end, pageable);
    List<OrderHistoryReportRow> content = page.getContent().stream()
        .map(r -> new OrderHistoryReportRow(r.getUpdatedAt(), r.getOrderId(), r.getStatus(), r.getStoreId(), r.getStoreName(), r.getTotalRevenue(), new java.util.ArrayList<>()))
        .collect(Collectors.toList());

    // Attach items per order in the page
    var orderIds = content.stream().map(OrderHistoryReportRow::getOrderId).distinct().toList();
    List<OrderItemRow> items = orderHistoryRepository.findItemsForOrders(orderIds);
    var byOrder = new java.util.HashMap<java.util.UUID, java.util.List<OrderItemInfo>>();
    for (OrderItemRow r : items) {
      var list = byOrder.computeIfAbsent(r.getOrderId(), k -> new java.util.ArrayList<>());
      list.add(new OrderItemInfo(r.getProductId(), r.getProductName(), r.getCategoryId(), r.getCategoryName(), r.getQuantity(), r.getPrice(), r.getRevenue()));
    }
    content.forEach(row -> row.setItems(byOrder.getOrDefault(row.getOrderId(), java.util.Collections.emptyList())));

    return PaginatedResponse.Utils.from(page, content);
  }

  @Override
  public List<MonthlyOrder> getMonthlyCount() {
    return orderHistoryRepository.findMonthlyOrderCounts();
  }
}


