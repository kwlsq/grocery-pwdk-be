package com.pwdk.grocereach.inventory.applications.implementation;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pwdk.grocereach.inventory.domains.interfaces.InventoryMonthlyReport;
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
        UUID storeId = userStoreId != null ? userStoreId : request.getStoreId();
        DateRange range = toDateRange(request.getMonth());

        List<InventoryMonthlyReport> reports = inventoryRepository.findAggregatedInventoryMonthlyReport( storeId, request.getWarehouseId(), request.getProductName(), range.start(), range.end()
        );

        List<StockReportSummaryResponse> summaries = createStockReportResponse(reports);

        int total = summaries.size();
        int start = Math.min((int) pageable.getOffset(), total);
        int end = Math.min(start + pageable.getPageSize(), total);
        List<StockReportSummaryResponse> content = start < end ? summaries.subList(start, end) : List.of();

        PageImpl<StockReportSummaryResponse> page = new PageImpl<>(content, pageable, total);
        return PaginatedResponse.Utils.from(page, content);
    }

    @Override
    public PaginatedResponse<StockReportDetailResponse> getProductStockReport(UUID productId, UUID userStoreId, UUID warehouseId, YearMonth month, Pageable pageable) {
      DateRange range = toDateRange(month);

        List<Inventory> records = inventoryRepository.findInventoryByProduct(productId, userStoreId, warehouseId, range.start(), range.end()
        );

        List<StockReportDetailResponse> details = records.stream()
            .map(this::toDetailResponse)
            .sorted(
                Comparator.comparing(StockReportDetailResponse::getProductVersion)
                    .thenComparing(StockReportDetailResponse::getWarehouseName)
                    .thenComparing(StockReportDetailResponse::getTimestamp)
            )
            .collect(Collectors.toList());

        int total = details.size();
        int start = Math.min((int) pageable.getOffset(), total);
        int end = Math.min(start + pageable.getPageSize(), total);
        List<StockReportDetailResponse> content = start < end ? details.subList(start, end) : List.of();

        Page<Inventory> meta = new PageImpl<>(records, pageable, total);
        return PaginatedResponse.Utils.from(meta, content);
    }

    public List<StockReportSummaryResponse> createStockReportResponse (List<InventoryMonthlyReport> list) {
        return list.stream()
            .map(StockReportSummaryResponse::from)
            .sorted(Comparator
                .comparing(StockReportSummaryResponse::getProductName)
                .thenComparing(StockReportSummaryResponse::getWarehouseName)
                .thenComparing(StockReportSummaryResponse::getMonth))
            .toList();
    }

    private StockReportDetailResponse toDetailResponse(Inventory inv) {
        String journal = inv.getJournal();
        String changeType = "ADDITION";
        int stockChange = 0;

        if (journal != null) {
            if (journal.startsWith("+")) {
                changeType = "ADDITION";
                stockChange = parseNumber(journal.substring(1));
            } else if (journal.startsWith("-")) {
                changeType = "REDUCTION";
                stockChange = parseNumber(journal.substring(1));
            }
        }
        return StockReportDetailResponse.from(inv, journal, changeType, stockChange);
    }

    private DateRange toDateRange(YearMonth month) {
        if (month == null) {
            return new DateRange(Instant.EPOCH, Instant.now().plusSeconds(1));
        }
        Instant start = month.atDay(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant();
        Instant end = month.plusMonths(1)
            .atDay(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant();
        return new DateRange(start, end);
    }

    private int parseNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private record DateRange(Instant start, Instant end) {}
}
