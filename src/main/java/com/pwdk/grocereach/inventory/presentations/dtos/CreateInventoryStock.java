package com.pwdk.grocereach.inventory.presentations.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryStock {
  private String warehouseID;
  private String productID;
  private Integer qty;
}
