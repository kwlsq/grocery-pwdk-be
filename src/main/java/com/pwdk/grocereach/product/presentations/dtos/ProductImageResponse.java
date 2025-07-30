package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.product.domains.entities.ProductImages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
  private UUID id;
  private String url;
  private boolean isPrimary;

  public static ProductImageResponse from(ProductImages productImages) {
    return new ProductImageResponse(
        productImages.getId(),
        productImages.getImageUrl(),
        productImages.isPrimary()
    );
  }
}
