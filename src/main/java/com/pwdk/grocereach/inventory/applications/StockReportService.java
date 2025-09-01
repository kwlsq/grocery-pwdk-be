package com.pwdk.grocereach.inventory.applications;

import java.util.List;
import java.util.UUID;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportDetailResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportRequest;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface StockReportService {
    PaginatedResponse<StockReportSummaryResponse> getMonthlyStockSummary(StockReportRequest request, UUID userStoreId, Pageable pageable);
    PaginatedResponse<StockReportDetailResponse> getMonthlyStockDetail(StockReportRequest request, UUID userStoreId, Pageable pageable);
}
