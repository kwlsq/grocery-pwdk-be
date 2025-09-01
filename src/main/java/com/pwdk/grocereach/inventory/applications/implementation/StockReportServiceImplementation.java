package com.pwdk.grocereach.inventory.applications.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.pwdk.grocereach.common.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public PaginatedResponse<StockReportSummaryResponse> getMonthlyStockSummary(StockReportRequest request, UUID userStoreId, Pageable pageable) {
        YearMonth month = request.getMonth() != null ? request.getMonth() : YearMonth.now();

        Instant startDate = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

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
                    .mapToInt(inv -> inv.getStock() > 0 ? inv.getStock() : 0)
                    .sum();

                int totalReduction = productInventories.stream()
                    .mapToInt(inv -> inv.getStock() < 0 ? Math.abs(inv.getStock()) : 0)
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
                    .month(month)
                    .totalAddition(totalAddition)
                    .totalReduction(totalReduction)
                    .finalStock(firstInv.getStock()) // or latest stock logic
                    .averagePrice(averagePrice)
                    .build();
            }).collect(Collectors.toList());

        return PaginatedResponse.Utils.from(inventoryPage, summaries);
    }

    @Override
    public PaginatedResponse<StockReportDetailResponse> getMonthlyStockDetail(StockReportRequest request, UUID userStoreId, Pageable pageable) {
        YearMonth month = request.getMonth() != null ? request.getMonth() : YearMonth.now();

        // Calculate date range for the month
        Instant startDate = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

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

        List<StockReportDetailResponse> summaryList = new ArrayList<>();

        for (Map.Entry<UUID, List<Inventory>> entry : groupedByProductVersion.entrySet()) {
            List<Inventory> productRecords = entry.getValue();
            Inventory sample = productRecords.get(0);

            // Opening stock: Get all inventory movements before startDate to calculate opening balance
            Page<Inventory> historicalInventory = inventoryRepository.findInventoryHistoryForReport(
                storeId,
                request.getWarehouseId(),
                null, // Don't filter by product name for historical data
                Instant.EPOCH, // From the beginning of time
                startDate, // Until start of reporting period
                Pageable.unpaged()
            );

            // Calculate opening stock by summing all movements before the period for this product version
            Long openingStock = historicalInventory.stream()
                .filter(inv -> inv.getProductVersion().getId().equals(entry.getKey()))
                .mapToLong(Inventory::getStock)
                .sum();

            // Calculate net additions and reductions during the period
            long totalAdditions = productRecords.stream()
                .filter(inv -> inv.getStock() > 0)
                .mapToLong(Inventory::getStock)
                .sum();

            long totalReductions = productRecords.stream()
                .filter(inv -> inv.getStock() < 0)
                .mapToLong(inv -> Math.abs(inv.getStock()))
                .sum();

            long closingStock = openingStock + totalAdditions - totalReductions;

            summaryList.add(StockReportDetailResponse.builder()
                .productName(sample.getProductVersion().getProduct().getName())
                .productVersion("v" + sample.getProductVersion().getVersionNumber())
                .storeName(sample.getWarehouse().getStore().getStoreName())
                .warehouseName(sample.getWarehouse().getName())
                .price(sample.getProductVersion().getPrice())
                .build());
        }

        // Sort the results for consistent ordering
        summaryList.sort(Comparator
            .comparing(StockReportDetailResponse::getProductName)
            .thenComparing(StockReportDetailResponse::getProductVersion));

        // Apply pagination manually after aggregation
        int totalElements = summaryList.size();
        int start = Math.min((int) pageable.getOffset(), totalElements);
        int end = Math.min(start + pageable.getPageSize(), totalElements);

        List<StockReportDetailResponse> paginatedList = start < totalElements ?
            summaryList.subList(start, end) : new ArrayList<>();

        return PaginatedResponse.Utils.from(inventoryList, paginatedList);
    }
}
