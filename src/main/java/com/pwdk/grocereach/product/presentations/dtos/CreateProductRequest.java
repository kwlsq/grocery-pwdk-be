package com.pwdk.grocereach.product.presentations.dtos;

import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
  private String name;
  private String description;
  private BigDecimal price;
  private BigDecimal weight;
  private Integer stock;
}
