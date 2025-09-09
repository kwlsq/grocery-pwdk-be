package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductPromotions;
import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import com.pwdk.grocereach.promotion.presentation.dto.PromotionResponse;
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
  private UUID categoryID;
  private ProductVersionResponse productVersionResponse;
  private List<ProductImageResponse> productImages;
  private List<PromotionResponse> promotions;

  public static ProductResponse from(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getCategory() != null ? product.getCategory().getId()
        : null,
        ProductVersionResponse.from(product.getCurrentVersion()),
        product.getProductImages() != null ? product.getProductImages().stream()
            .map(ProductImageResponse::from)
            .toList() : null,
        product.getProductPromotions() != null
        ? product.getProductPromotions().stream()
            .map(pp -> PromotionResponse.from(pp.getPromotion()))
            .toList()
        : null
    );
  }
}
