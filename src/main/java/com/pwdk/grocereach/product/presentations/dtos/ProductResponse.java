package com.pwdk.grocereach.product.presentations.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private UUID id;
  private String name;
  private String description;
  private ProductVersionResponse productVersionResponse;
}
