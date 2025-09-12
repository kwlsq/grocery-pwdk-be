package com.pwdk.grocereach.inventory.applications;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportDetailResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportRequest;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportSummaryResponse;

public interface StockReportService {
    PaginatedResponse<StockReportSummaryResponse> getMonthlyStockSummary(StockReportRequest request, UUID userStoreId, Pageable pageable);
    PaginatedResponse<StockReportDetailResponse> getProductStockReport(UUID productId, UUID userStoreId, UUID warehouseId, java.time.YearMonth month, Pageable pageable);
}
