package com.pwdk.grocereach.inventory.applications.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.inventory.applications.StockReportService;
import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportDetailResponse;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportRequest;
import com.pwdk.grocereach.inventory.presentations.dtos.StockReportSummaryResponse;

@Service
public class StockReportServiceImplementation implements StockReportService {

    private final InventoryRepository inventoryRepository;

    public StockReportServiceImplementation (InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public PaginatedResponse<StockReportSummaryResponse> getMonthlyStockSummary(StockReportRequest request, UUID userStoreId, Pageable pageable) {
        YearMonth month = request.getMonth() != null ? request.getMonth() : null;
        
        Instant startDate, endDate;
        
        if (month != null) {
            // If specific month is requested, use that month's date range
            startDate = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            endDate = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else {
            // If no month specified, show all records (from beginning of time to now)
            startDate = Instant.EPOCH; // Beginning of time
            endDate = Instant.now().plusSeconds(1); // Current time + 1 second
        }

        // Use userStoreId if available, otherwise use request.getStoreId(), otherwise null (no filter)
        UUID storeId = userStoreId != null ? userStoreId : request.getStoreId();

        Page<Inventory> inventoryPage = inventoryRepository.findInventoryHistoryForReport(
            storeId,
            request.getWarehouseId(),
            request.getProductName(),
            startDate,
            endDate,
            pageable
        );

        List<StockReportSummaryResponse> summaries = inventoryPage.getContent().stream()
            .collect(Collectors.groupingBy(inv -> inv.getProductVersion().getId() + "_" + inv.getWarehouse().getId()))
            .entrySet().stream()
            .map(entry -> {
                List<Inventory> productInventories = entry.getValue();
                Inventory firstInv = productInventories.get(0);

                int totalAddition = productInventories.stream()
                    .mapToInt(inv -> {
                        String journal = inv.getJournal();
                        if (journal != null && journal.startsWith("+")) {
                            try {
                                return Integer.parseInt(journal.substring(1));
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                        return 0;
                    })
                    .sum();

                int totalReduction = productInventories.stream()
                    .mapToInt(inv -> {
                        String journal = inv.getJournal();
                        if (journal != null && journal.startsWith("-")) {
                            try {
                                return Integer.parseInt(journal.substring(1));
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                        return 0;
                    })
                    .sum();

                BigDecimal averagePrice = productInventories.stream()
                    .map(inv -> inv.getProductVersion().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(productInventories.size()), 2, RoundingMode.HALF_UP);

                return StockReportSummaryResponse.builder()
                    .productName(firstInv.getProductVersion().getProduct().getName())
                    .productVersion("v" + firstInv.getProductVersion().getVersionNumber())
                    .storeName(firstInv.getWarehouse().getStore().getStoreName())
                    .warehouseName(firstInv.getWarehouse().getName())
                    .month(month) // This will be null if showing all records
                    .totalAddition(totalAddition)
                    .totalReduction(totalReduction)
                    .finalStock(firstInv.getStock()) // This is the final cumulative stock
                    .averagePrice(averagePrice)
                    .build();
            }).collect(Collectors.toList());

        return PaginatedResponse.Utils.from(inventoryPage, summaries);
    }

    @Override
    public PaginatedResponse<StockReportDetailResponse> getMonthlyStockDetail(StockReportRequest request, UUID userStoreId, Pageable pageable) {
        YearMonth month = request.getMonth() != null ? request.getMonth() : null;

        // Calculate date range for the month
        Instant startDate, endDate;
        
        if (month != null) {
            // If specific month is requested, use that month's date range
            startDate = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            endDate = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else {
            // If no month specified, show all records (from beginning of time to now)
            startDate = Instant.EPOCH; // Beginning of time
            endDate = Instant.now().plusSeconds(1); // Current time + 1 second
        }

        // Apply store filter based on user role
        UUID storeId = userStoreId != null ? userStoreId : request.getStoreId();

        // Fetch inventory records for this period
        Page<Inventory> inventoryList = inventoryRepository.findInventoryHistoryForReport(
            storeId,
            request.getWarehouseId(),
            request.getProductName(),
            startDate,
            endDate,
            Pageable.unpaged() // We aggregate before paging
        );

        // Group by Product + Version
        Map<UUID, List<Inventory>> groupedByProductVersion = inventoryList.stream()
            .collect(Collectors.groupingBy(inv -> inv.getProductVersion().getId()));

        List<StockReportDetailResponse> detailList = new ArrayList<>();

        for (Map.Entry<UUID, List<Inventory>> entry : groupedByProductVersion.entrySet()) {
            List<Inventory> productRecords = entry.getValue();
            
            // Create a detail record for each inventory movement
            for (Inventory inv : productRecords) {
                String journal = inv.getJournal();
                String changeType = "ADDITION";
                Integer stockChange = 0;
                
                if (journal != null) {
                    if (journal.startsWith("+")) {
                        changeType = "ADDITION";
                        try {
                            stockChange = Integer.parseInt(journal.substring(1));
                        } catch (NumberFormatException e) {
                            stockChange = 0;
                        }
                    } else if (journal.startsWith("-")) {
                        changeType = "REDUCTION";
                        try {
                            stockChange = Integer.parseInt(journal.substring(1));
                        } catch (NumberFormatException e) {
                            stockChange = 0;
                        }
                    }
                }

                detailList.add(StockReportDetailResponse.builder()
                    .productName(inv.getProductVersion().getProduct().getName())
                    .productVersion("v" + inv.getProductVersion().getVersionNumber())
                    .storeName(inv.getWarehouse().getStore().getStoreName())
                    .warehouseName(inv.getWarehouse().getName())
                    .stockChange(stockChange)
                    .journal(journal)
                    .timestamp(inv.getCreatedAt())
                    .price(inv.getProductVersion().getPrice())
                    .changeType(changeType)
                    .build());
            }
        }

        // Sort the results for consistent ordering
        detailList.sort(Comparator
            .comparing(StockReportDetailResponse::getProductName)
            .thenComparing(StockReportDetailResponse::getProductVersion)
            .thenComparing(StockReportDetailResponse::getTimestamp));

        // Apply pagination manually after aggregation
        int totalElements = detailList.size();
        int start = Math.min((int) pageable.getOffset(), totalElements);
        int end = Math.min(start + pageable.getPageSize(), totalElements);

        List<StockReportDetailResponse> paginatedList = start < totalElements ?
            detailList.subList(start, end) : new ArrayList<>();

        return PaginatedResponse.Utils.from(inventoryList, paginatedList);
    }
}
