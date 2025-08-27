package com.pwdk.grocereach.inventory.presentations.dtos;

import java.math.BigDecimal;
import java.time.YearMonth;

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
    private String productVersion;
    private String storeName;
    private String warehouseName;
    private YearMonth month;
    private Integer totalAddition;
    private Integer totalReduction;
    private Integer finalStock;
    private BigDecimal averagePrice;
}
