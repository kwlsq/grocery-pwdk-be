package com.pwdk.grocereach.product.applications.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pwdk.grocereach.inventory.presentations.dtos.InventoryResponse;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;

@Component
public class ProductDistanceFilterService {

  public record FilteredProductsResult(List<Product> products, List<ProductResponse> responses) {}

  public FilteredProductsResult filterProductsByDistance(List<Product> products,
                                                         double userLatitude,
                                                         double userLongitude,
                                                         double maxDistanceKM) {
    List<Product> filteredProducts = new ArrayList<>();
    List<ProductResponse> filteredResponses = new ArrayList<>();

    for (Product product : products) {
      ProductResponse response = ProductResponse.from(product);

      List<InventoryResponse> inventories = response.getProductVersionResponse().getInventories();
      if (inventories == null) {
        response.getProductVersionResponse().setInventories(List.of());
      } else {
        List<InventoryResponse> filteredInventories = inventories.stream()
            .filter(inv -> withinRange(userLatitude, userLongitude, inv.getWarehouseLatitude(), inv.getWarehouseLongitude(), maxDistanceKM))
            .toList();
        response.getProductVersionResponse().setInventories(filteredInventories);
      }

      if (response.getProductVersionResponse().getInventories() != null
          && !response.getProductVersionResponse().getInventories().isEmpty()) {
        filteredProducts.add(product);
        filteredResponses.add(response);
      }
    }

    return new FilteredProductsResult(filteredProducts, filteredResponses);
  }

  private boolean withinRange(double lat1, double lon1, double lat2, double lon2, double maxKm) {
    return haversine(lat1, lon1, lat2, lon2) <= maxKm;
  }

  private double haversine(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // km
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }
}



