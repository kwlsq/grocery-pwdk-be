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
import org.springframework.data.domain.PageImpl;
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

        // Fetch all matching inventory records within the date range (no DISTINCT), so we can aggregate
        List<Inventory> inventoryAll = inventoryRepository.findInventoryForReportAll(
            storeId,
            request.getWarehouseId(),
            request.getProductName(),
            startDate,
            endDate
        );

        // Group by productVersion + warehouse + month (derived from createdAt when month filter not provided)
        Map<String, List<Inventory>> grouped = inventoryAll.stream()
            .collect(Collectors.groupingBy(inv -> {
                UUID productVersionId = inv.getProductVersion().getId();
                UUID warehouseId = inv.getWarehouse().getId();
                YearMonth groupMonth = (month != null)
                    ? month
                    : YearMonth.from(inv.getCreatedAt().atZone(ZoneId.systemDefault()));
                return productVersionId + "_" + warehouseId + "_" + groupMonth;
            }));

        List<StockReportSummaryResponse> summaries = grouped.values().stream().map(productInventories -> {
            // Determine the month for this group
            YearMonth groupMonth = (month != null)
                ? month
                : YearMonth.from(productInventories.get(0).getCreatedAt().atZone(ZoneId.systemDefault()));

            // Compute totals
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

            // Final stock is the last record's stock within this month for the product+warehouse
            Inventory lastInvInMonth = productInventories.stream()
                .max(Comparator.comparing(Inventory::getCreatedAt))
                .orElse(productInventories.get(0));

            Inventory sample = lastInvInMonth;

            return StockReportSummaryResponse.builder()
                .productName(sample.getProductVersion().getProduct().getName())
                .productID(sample.getProductVersion().getProduct().getId())
                .productVersion("v" + sample.getProductVersion().getVersionNumber())
                .storeName(sample.getWarehouse().getStore().getStoreName())
                .warehouseName(sample.getWarehouse().getName())
                .month(groupMonth)
                .totalAddition(totalAddition)
                .totalReduction(totalReduction)
                .finalStock(sample.getStock())
                .averagePrice(averagePrice)
                .build();
        }).sorted(
            Comparator.comparing(StockReportSummaryResponse::getProductName)
                .thenComparing(StockReportSummaryResponse::getProductVersion)
                .thenComparing(StockReportSummaryResponse::getWarehouseName)
                .thenComparing(StockReportSummaryResponse::getMonth)
        ).collect(Collectors.toList());

        // Apply pagination manually to the aggregated summaries
        int totalElements = summaries.size();
        int start = Math.min((int) pageable.getOffset(), totalElements);
        int end = Math.min(start + pageable.getPageSize(), totalElements);
        List<StockReportSummaryResponse> paginatedSummaries = start < totalElements ? summaries.subList(start, end) : new ArrayList<>();

        // Build a synthetic Page carrying the pagination metadata
        Page<Inventory> pageMeta = new PageImpl<>(inventoryAll, pageable, totalElements);
        return PaginatedResponse.Utils.from(pageMeta, paginatedSummaries);
    }

    @Override
    public PaginatedResponse<StockReportDetailResponse> getProductStockReport(UUID productId, UUID userStoreId, UUID warehouseId, YearMonth month, Pageable pageable) {
        UUID storeId = userStoreId; // managers constrained by their store when available

        Instant startDate;
        Instant endDate;
        if (month != null) {
            startDate = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            endDate = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else {
            startDate = Instant.EPOCH;
            endDate = Instant.now().plusSeconds(1);
        }

        List<Inventory> records = inventoryRepository.findInventoryByProduct(
            productId,
            storeId,
            warehouseId,
            startDate,
            endDate
        );

        List<StockReportDetailResponse> details = records.stream().map(inv -> {
            String journal = inv.getJournal();
            String changeType = "ADDITION";
            Integer stockChange = 0;

            if (journal != null) {
                if (journal.startsWith("+")) {
                    changeType = "ADDITION";
                    try { stockChange = Integer.parseInt(journal.substring(1)); } catch (NumberFormatException e) { stockChange = 0; }
                } else if (journal.startsWith("-")) {
                    changeType = "REDUCTION";
                    try { stockChange = Integer.parseInt(journal.substring(1)); } catch (NumberFormatException e) { stockChange = 0; }
                }
            }

            return StockReportDetailResponse.builder()
                .productName(inv.getProductVersion().getProduct().getName())
                .productVersion("v" + inv.getProductVersion().getVersionNumber())
                .storeName(inv.getWarehouse().getStore().getStoreName())
                .warehouseName(inv.getWarehouse().getName())
                .stockChange(stockChange)
                .journal(journal)
                .timestamp(inv.getCreatedAt())
                .price(inv.getProductVersion().getPrice())
                .changeType(changeType)
                .build();
        }).sorted(
            Comparator.comparing(StockReportDetailResponse::getProductVersion)
                .thenComparing(StockReportDetailResponse::getWarehouseName)
                .thenComparing(StockReportDetailResponse::getTimestamp)
        ).collect(Collectors.toList());

        int total = details.size();
        int start = Math.min((int) pageable.getOffset(), total);
        int end = Math.min(start + pageable.getPageSize(), total);
        List<StockReportDetailResponse> pageContent = start < total ? details.subList(start, end) : new ArrayList<>();

        Page<Inventory> meta = new PageImpl<>(records, pageable, total);
        return PaginatedResponse.Utils.from(meta, pageContent);
    }
}
