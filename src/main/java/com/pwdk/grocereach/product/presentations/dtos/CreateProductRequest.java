package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
  @NotBlank(message = "Name cannot be blank")
  private String name;

  @NotBlank(message = "Description cannot be blank")
  private String description;

  @NotNull(message = "Price is required")
  private BigDecimal price;

  @NotNull(message = "Weight is required")
  private BigDecimal weight;

  @NotBlank(message = "Category ID is required")
  private String categoryID;

  @NotBlank(message = "Store ID is required")
  private String storeID;

  @NotBlank(message = "Inventories are required")
  private List<WarehouseStock> inventories;

  private List<UpdateProductRequest.AttachPromotion> promotions;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AttachPromotion {
    private String promotionID;
  }
}
