package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

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

  @NotNull(message = "Stock is required")
  private Integer stock;

  @NotBlank(message = "Category ID is required")
  private String categoryID;
}
