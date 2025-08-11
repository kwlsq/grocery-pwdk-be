package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.product.domains.entities.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {
  private UUID id;
  private UUID parentID;
  private String name;

  public static ProductCategoryResponse from(ProductCategory productCategory) {
    return new ProductCategoryResponse(
        productCategory.getId(),
        productCategory.getParent() != null ? productCategory.getParent().getId() : null,
        productCategory.getName()
    );
  }
}
