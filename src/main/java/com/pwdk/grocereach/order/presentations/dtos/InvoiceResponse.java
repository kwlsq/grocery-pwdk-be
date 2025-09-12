package com.pwdk.grocereach.order.presentations.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private UUID orderId;
    private UUID userId;
    private UUID addressId;
    private String status;
    private BigDecimal totalPrice;
    private Instant orderedAt;
    private List<InvoiceResponseItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceResponseItem {
        private String productName;
        private String productVersion;
        private String warehouseName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subTotal;
    }
}


