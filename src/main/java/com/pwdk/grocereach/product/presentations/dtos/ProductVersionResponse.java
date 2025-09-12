package com.pwdk.grocereach.product.presentations.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.pwdk.grocereach.inventory.presentations.dtos.InventoryResponse;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVersionResponse {
  private UUID id;
  private Integer versionNumber;
  private BigDecimal price;
  private BigDecimal weight;
  private List<InventoryResponse> inventories;

  public static ProductVersionResponse from(ProductVersions version) {
    return new ProductVersionResponse(
        version.getId(),
        version.getVersionNumber(),
        version.getPrice(),
        version.getWeight(),
        version.getInventories() != null ? version.getInventories().stream()
            .map(InventoryResponse::from)
            .toList() : null
    );
  }
}
