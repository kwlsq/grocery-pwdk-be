package com.pwdk.grocereach.inventory.presentations;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
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
    private final InventoryRepository inventoryRepository;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> getMonthlyStockSummary(
            @RequestParam(required = false, value = "storeId") UUID storeId,
            @RequestParam(required = false, value = "warehouseId") UUID warehouseId,
            @RequestParam(required = false, value = "month") String month,
            @RequestParam(required = false, value = "productName") String productName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            UUID userStoreId = getUserStoreId();

            YearMonth yearMonth = month != null && !month.trim().isEmpty() ? yearMonthConverter.convert(month) : null;

            Pageable pageable = PageRequest.of(page,size);
            
            StockReportRequest request = StockReportRequest.builder()
                    .storeId(storeId)
                    .warehouseId(warehouseId)
                    .month(yearMonth)
                    .productName(productName != null && !productName.trim().isEmpty() ? productName.trim() : null)
                    .build();
            
            return Response.successfulResponse(
                "Stock summary report retrieved successfully",
                stockReportService.getMonthlyStockSummary(request, userStoreId, pageable)
                );
                    
        } catch (Exception e) {
            log.error("Error retrieving stock summary report", e);
            return Response.failedResponse("Failed to retrieve stock summary report: " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getMonthlyStockDetail(
        @RequestParam(required = false, value = "storeId") UUID storeId,
        @RequestParam(required = false, value = "warehouseId") UUID warehouseId,
        @RequestParam(required = false, value = "month") String month,
        @RequestParam(required = false, value = "productName") String productName,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            UUID userStoreId = getUserStoreId();

            YearMonth yearMonth = month != null && !month.trim().isEmpty() ? yearMonthConverter.convert(month) : null;

            Pageable pageable = PageRequest.of(page, size);
            
            StockReportRequest request = StockReportRequest.builder()
                    .storeId(storeId)
                    .warehouseId(warehouseId)
                    .month(yearMonth)
                    .productName(productName != null && !productName.trim().isEmpty() ? productName.trim() : null)
                    .build();

            return Response.successfulResponse(
                "Stock detail report retrieved successfully",
                stockReportService.getMonthlyStockDetail(request, userStoreId, pageable)
                );
                    
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

    // Debug endpoint to test inventory data
    @GetMapping("/debug")
    public ResponseEntity<?> debugInventoryData() {
        try {
            long totalCount = inventoryRepository.countAllActiveInventory();
            List<Inventory> allInventory = inventoryRepository.findInventoryWithoutDateFilter(null, null, null);
            
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("totalInventoryCount", totalCount);
            debugInfo.put("inventoryWithoutDateFilter", allInventory.size());
            
            if (!allInventory.isEmpty()) {
                debugInfo.put("firstRecordCreatedAt", allInventory.get(0).getCreatedAt());
                debugInfo.put("lastRecordCreatedAt", allInventory.get(allInventory.size() - 1).getCreatedAt());
                debugInfo.put("sampleRecord", Map.of(
                    "id", allInventory.get(0).getId(),
                    "stock", allInventory.get(0).getStock(),
                    "warehouseId", allInventory.get(0).getWarehouse().getId(),
                    "productVersionId", allInventory.get(0).getProductVersion().getId(),
                    "productName", allInventory.get(0).getProductVersion().getProduct().getName()
                ));
            }
            
            return Response.successfulResponse("Debug information retrieved successfully", debugInfo);
        } catch (Exception e) {
            log.error("Error in debug endpoint", e);
            return Response.failedResponse("Debug failed: " + e.getMessage());
        }
    }
}
