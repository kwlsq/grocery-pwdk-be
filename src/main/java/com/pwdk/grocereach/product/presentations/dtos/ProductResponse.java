package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.product.domains.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private UUID id;
  private String name;
  private String description;
  private ProductVersionResponse productVersionResponse;
  private List<ProductImageResponse> productImages;

  public static ProductResponse from(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        ProductVersionResponse.from(product.getCurrentVersion()),
        product.getProductImages() != null ? product.getProductImages().stream()
            .map(ProductImageResponse::from)
            .toList() : null
    );
  }
}
