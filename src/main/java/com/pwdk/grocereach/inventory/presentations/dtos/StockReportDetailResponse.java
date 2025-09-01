package com.pwdk.grocereach.inventory.presentations.dtos;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReportDetailResponse {
    private String productName;
    private String productVersion;
    private String storeName;
    private String warehouseName;
    private Integer stockChange;
    private String journal;
    private Instant timestamp;
    private BigDecimal price;
    private String changeType; // "ADDITION" or "REDUCTION"
}
