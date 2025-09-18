package com.pwdk.grocereach.inventory.presentations;

import java.time.YearMonth;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.inventory.applications.StockReportService;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportRequest;
import com.pwdk.grocereach.inventory.presentations.dtos.YearMonthConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/stock-reports")
@RequiredArgsConstructor
@Slf4j
public class StockReportController {

    private final StockReportService stockReportService;
    private final YearMonthConverter yearMonthConverter;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getMonthlyStockSummary(
            @RequestParam(required = false, value = "storeId") UUID storeId,
            @RequestParam(required = false, value = "warehouseId") UUID warehouseId,
            @RequestParam(required = false, value = "month") String month,
            @RequestParam(required = false, value = "productName") String productName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            YearMonth yearMonth = month != null && !month.trim().isEmpty() ? yearMonthConverter.convert(month) : null;
            Pageable pageable = PageRequest.of(page,size);
            String product = productName != null && !productName.trim().isEmpty() ? productName.trim() : null;
            StockReportRequest request = StockReportRequest.from(storeId, warehouseId, yearMonth, product);

            return Response.successfulResponse(
                "Stock summary report retrieved successfully",
                stockReportService.getMonthlyStockSummary(request, null, pageable)
                );
                    
        } catch (Exception e) {
            log.error("Error retrieving stock summary report", e);
            return Response.failedResponse("Failed to retrieve stock summary report: " + e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getProductStockReport(
        @PathVariable("productId") UUID productId,
        @RequestParam(required = false, value = "warehouseId") UUID warehouseId,
        @RequestParam(required = false, value = "storeId") UUID storeId,
        @RequestParam(required = false, value = "month") String month,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            YearMonth yearMonth = month != null && !month.trim().isEmpty() ? yearMonthConverter.convert(month) : null;
            return Response.successfulResponse(
                "Product stock report retrieved successfully",
                stockReportService.getProductStockReport(productId, storeId, warehouseId, yearMonth, pageable)
            );
        } catch (Exception e) {
            log.error("Error retrieving product stock report", e);
            return Response.failedResponse("Failed to retrieve product stock report: " + e.getMessage());
        }
    }
}
