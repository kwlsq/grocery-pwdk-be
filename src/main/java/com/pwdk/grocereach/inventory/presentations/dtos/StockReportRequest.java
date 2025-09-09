package com.pwdk.grocereach.inventory.presentations.dtos;

import java.time.YearMonth;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReportRequest {
    private UUID storeId;
    private UUID warehouseId;
    private YearMonth month;
    private String productName;
}
