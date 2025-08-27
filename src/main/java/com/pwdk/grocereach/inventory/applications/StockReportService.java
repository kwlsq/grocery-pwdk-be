package com.pwdk.grocereach.inventory.applications;

import java.util.List;
import java.util.UUID;

import com.pwdk.grocereach.inventory.presentations.dtos.StockReportDetailResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportRequest;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportSummaryResponse;

public interface StockReportService {
    List<StockReportSummaryResponse> getMonthlyStockSummary(StockReportRequest request, UUID userStoreId);
    List<StockReportDetailResponse> getMonthlyStockDetail(StockReportRequest request, UUID userStoreId);
}
