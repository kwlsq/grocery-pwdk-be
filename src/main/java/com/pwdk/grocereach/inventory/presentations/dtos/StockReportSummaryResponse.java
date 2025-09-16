package com.pwdk.grocereach.inventory.presentations.dtos;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

import com.pwdk.grocereach.inventory.domains.interfaces.InventoryMonthlyReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReportSummaryResponse {
    private String productName;
    private UUID productID;
    private String productVersion;
    private String storeName;
    private String warehouseName;
    private YearMonth month;
    private Integer totalAddition;
    private Integer totalReduction;
    private Integer finalStock;
    private BigDecimal averagePrice;

    public static StockReportSummaryResponse from(InventoryMonthlyReport report) {
        return new StockReportSummaryResponse(
            report.getProductName(),
            report.getProductId(),
            ("v" + report.getLatestVersion()),
            report.getStoreName(),
            report.getWarehouseName(),
            YearMonth.parse(report.getMonth()),
            report.getTotalAddition(),
            report.getTotalReduction(),
            report.getFinalStock(),
            report.getAveragePrice()
        );
    }
}
