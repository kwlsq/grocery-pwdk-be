package com.pwdk.grocereach.inventory.applications.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pwdk.grocereach.inventory.applications.StockReportService;
import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportDetailResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportRequest;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportSummaryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
public class StockReportServiceImplementation implements StockReportService {

    private final InventoryRepository inventoryRepository;

    public StockReportServiceImplementation (InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<StockReportSummaryResponse> getMonthlyStockSummary(StockReportRequest request, UUID userStoreId) {
        // Set default month to current month if not specified
        YearMonth month = request.getMonth() != null ? request.getMonth() : YearMonth.now();
        
        // Calculate date range for the month
        Instant startDate = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        // Apply store filter based on user role
        UUID storeId = userStoreId != null ? userStoreId : request.getStoreId();
        
        // Get inventory history for the month
        List<Inventory> inventoryHistory = inventoryRepository.findInventoryHistoryForReport(
                storeId, request.getWarehouseId(), request.getProductName(), startDate, endDate
        );
        
        // Get current inventory at the end of the month
        List<Inventory> currentInventory = inventoryRepository.findCurrentInventoryForReport(
                storeId, request.getWarehouseId(), request.getProductName(), startDate, endDate
        );
        
        // Group by product version and warehouse
        Map<String, List<Inventory>> groupedHistory = inventoryHistory.stream()
                .collect(Collectors.groupingBy(inv -> 
                        inv.getProductVersion().getProduct().getId() + "_" + 
                        inv.getProductVersion().getId() + "_" + 
                        inv.getWarehouse().getId()
                ));
        
        List<StockReportSummaryResponse> summaries = new ArrayList<>();
        
        for (Map.Entry<String, List<Inventory>> entry : groupedHistory.entrySet()) {
            List<Inventory> productInventories = entry.getValue();
            if (productInventories.isEmpty()) continue;
            
            Inventory firstInv = productInventories.get(0);
            
            // Calculate totals
            int totalAddition = productInventories.stream()
                    .mapToInt(inv -> inv.getStock() > 0 ? inv.getStock() : 0)
                    .sum();
            
            int totalReduction = productInventories.stream()
                    .mapToInt(inv -> inv.getStock() < 0 ? Math.abs(inv.getStock()) : 0)
                    .sum();
            
            // Find final stock from current inventory
            int finalStock = currentInventory.stream()
                    .filter(inv -> inv.getProductVersion().getId().equals(firstInv.getProductVersion().getId()) &&
                                 inv.getWarehouse().getId().equals(firstInv.getWarehouse().getId()))
                    .mapToInt(Inventory::getStock)
                    .findFirst()
                    .orElse(0);
            
            // Calculate average price
            BigDecimal averagePrice = productInventories.stream()
                    .map(inv -> inv.getProductVersion().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(productInventories.size()), 2, RoundingMode.HALF_UP);
            
            StockReportSummaryResponse summary = StockReportSummaryResponse.builder()
                    .productName(firstInv.getProductVersion().getProduct().getName())
                    .productVersion("v" + firstInv.getProductVersion().getVersionNumber())
                    .storeName(firstInv.getWarehouse().getStore().getStoreName())
                    .warehouseName(firstInv.getWarehouse().getName())
                    .month(month)
                    .totalAddition(totalAddition)
                    .totalReduction(totalReduction)
                    .finalStock(finalStock)
                    .averagePrice(averagePrice)
                    .build();
            
            summaries.add(summary);
        }
        
        return summaries;
    }

    @Override
    public List<StockReportDetailResponse> getMonthlyStockDetail(StockReportRequest request, UUID userStoreId) {
        // Set default month to current month if not specified
        YearMonth month = request.getMonth() != null ? request.getMonth() : YearMonth.now();
        
        // Calculate date range for the month
        Instant startDate = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        // Apply store filter based on user role
        UUID storeId = userStoreId != null ? userStoreId : request.getStoreId();
        
        // Get inventory history for the month
        List<Inventory> inventoryHistory = inventoryRepository.findInventoryHistoryForReport(
                storeId, request.getWarehouseId(), request.getProductName(), startDate, endDate
        );
        
        return inventoryHistory.stream()
                .map(inv -> StockReportDetailResponse.builder()
                        .productName(inv.getProductVersion().getProduct().getName())
                        .productVersion("v" + inv.getProductVersion().getVersionNumber())
                        .storeName(inv.getWarehouse().getStore().getStoreName())
                        .warehouseName(inv.getWarehouse().getName())
                        .stockChange(inv.getStock())
                        .journal(inv.getJournal())
                        .timestamp(inv.getCreatedAt())
                        .price(inv.getProductVersion().getPrice())
                        .changeType(inv.getStock() > 0 ? "ADDITION" : "REDUCTION")
                        .build())
                .collect(Collectors.toList());
    }
}
