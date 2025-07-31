package com.pwdk.grocereach.image.presentations.dtos;

import com.pwdk.grocereach.image.domains.entities.ProductImages;
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
