package com.pwdk.grocereach.product.applications.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
      if (inventories == null || inventories.isEmpty()) {
        // Skip products with no inventory
        continue;
      }

      // Filter inventories within range and that are not deleted
      List<InventoryResponse> availableInventories = inventories.stream()
          .filter(inv -> inv.getDeletedAt() == null) // Only non-deleted inventories
          .filter(inv -> withinRange(userLatitude, userLongitude,
              inv.getWarehouseLatitude(),
              inv.getWarehouseLongitude(),
              maxDistanceKM))
          .toList();

      if (!availableInventories.isEmpty()) {
        // Find the nearest inventory
        Optional<InventoryResponse> nearestInventory = availableInventories.stream()
            .min((inv1, inv2) -> {
              double distance1 = haversine(userLatitude, userLongitude,
                  inv1.getWarehouseLatitude(),
                  inv1.getWarehouseLongitude());
              double distance2 = haversine(userLatitude, userLongitude,
                  inv2.getWarehouseLatitude(),
                  inv2.getWarehouseLongitude());
              return Double.compare(distance1, distance2);
            });

        if (nearestInventory.isPresent()) {
          // Set only the nearest inventory
          response.getProductVersionResponse().setInventories(List.of(nearestInventory.get()));
          filteredProducts.add(product);
          filteredResponses.add(response);
        }
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