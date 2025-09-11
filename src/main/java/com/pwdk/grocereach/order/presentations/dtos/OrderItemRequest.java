package com.pwdk.grocereach.order.presentations.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private UUID productVersionId;
    private UUID warehouseId;
    private Integer quantity;
}


