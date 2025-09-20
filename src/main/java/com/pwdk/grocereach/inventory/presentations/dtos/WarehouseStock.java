package com.pwdk.grocereach.inventory.presentations.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStock {
  private String warehouseID;
  private Integer stock;
}
