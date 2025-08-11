package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.presentations.dtos.InventoryResponse;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVersionResponse {
  private Integer versionNumber;
  private BigDecimal price;
  private BigDecimal weight;
  private List<InventoryResponse> inventories;

  public static ProductVersionResponse from(ProductVersions version) {
    return new ProductVersionResponse(
        version.getVersionNumber(),
        version.getPrice(),
        version.getWeight(),
        version.getInventories().stream()
            .map(InventoryResponse::from)
            .toList()
    );
  }
}
