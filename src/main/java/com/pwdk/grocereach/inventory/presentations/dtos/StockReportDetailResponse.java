package com.pwdk.grocereach.inventory.presentations.dtos;

import java.math.BigDecimal;
import java.time.Instant;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
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
    private String changeType;

    public static StockReportDetailResponse from (Inventory inv, String journal, String changeType, int stockChange) {
        return new StockReportDetailResponse(
            inv.getProductVersion().getProduct().getName(),
            "v" + inv.getProductVersion().getVersionNumber(),
            inv.getWarehouse().getStore().getStoreName(),
            inv.getWarehouse().getName(),
            stockChange,
            journal,
            inv.getCreatedAt(),
            inv.getProductVersion().getPrice(),
            changeType
        );
    }
}
