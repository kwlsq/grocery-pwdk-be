package com.pwdk.grocereach.inventory.presentations;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.inventory.applications.StockReportService;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportDetailResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportRequest;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportSummaryResponse;
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
    public ResponseEntity<Response<List<StockReportSummaryResponse>>> getMonthlyStockSummary(
            @RequestParam(required = false, value = "storeId", defaultValue = "") UUID storeId,
            @RequestParam(required = false, value = "warehouseId", defaultValue = "") UUID warehouseId,
            @RequestParam(required = false, value = "month", defaultValue = "") String month,
            @RequestParam(required = false, value = "productName" , defaultValue = "") String productName) {
        
        try {
            UUID userStoreId = getUserStoreId();

            YearMonth yearMonth = month != null ? yearMonthConverter.convert(month) : null;
            
            StockReportRequest request = StockReportRequest.builder()
                    .storeId(storeId)
                    .warehouseId(warehouseId)
                    .month(yearMonth)
                    .productName(productName)
                    .build();
            
            List<StockReportSummaryResponse> summary = stockReportService.getMonthlyStockSummary(request, userStoreId);
            
            return Response.successfulResponse("Stock summary report retrieved successfully", summary);
                    
        } catch (Exception e) {
            log.error("Error retrieving stock summary report", e);
            return Response.failedResponse("Failed to retrieve stock summary report: " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Response<List<StockReportDetailResponse>>> getMonthlyStockDetail(
        @RequestParam(required = false, value = "storeId", defaultValue = "") UUID storeId,
        @RequestParam(required = false, value = "warehouseId", defaultValue = "") UUID warehouseId,
        @RequestParam(required = false, value = "month", defaultValue = "") String month,
        @RequestParam(required = false, value = "productName" , defaultValue = "") String productName) {
        
        try {
            UUID userStoreId = getUserStoreId();

            YearMonth yearMonth = month != null ? yearMonthConverter.convert(month) : null;
            
            StockReportRequest request = StockReportRequest.builder()
                    .storeId(storeId)
                    .warehouseId(warehouseId)
                    .month(yearMonth)
                    .productName(productName)
                    .build();
            
            List<StockReportDetailResponse> detail = stockReportService.getMonthlyStockDetail(request, userStoreId);
            
            return Response.successfulResponse("Stock detail report retrieved successfully", detail);
                    
        } catch (Exception e) {
            log.error("Error retrieving stock detail report", e);
            return Response.failedResponse("Failed to retrieve stock detail report: " + e.getMessage());
        }
    }

    private UUID getUserStoreId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isManager = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"));
            
            if (isManager) {
                // For manager users, we need to get their store ID from the user context
                // This would typically come from a custom user details service
                // For now, we'll return null and let the service handle it
                // TODO: Implement proper user store mapping
                return null;
            }
        }
        return null;
    }
}
