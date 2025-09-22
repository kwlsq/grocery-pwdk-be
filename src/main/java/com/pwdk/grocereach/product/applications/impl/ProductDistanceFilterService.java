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

  public FilteredProductsResult filterProductsByDistance(List<Product> products, double userLatitude, double userLongitude, double maxDistanceKM) {
    List<Product> filteredProducts = new ArrayList<>();
    List<ProductResponse> filteredResponses = new ArrayList<>();

    for (Product product : products) {
      processProduct(product, userLatitude, userLongitude, maxDistanceKM,
          filteredProducts, filteredResponses);
    }

    return new FilteredProductsResult(filteredProducts, filteredResponses);
  }

  private void processProduct(Product product, double userLatitude, double userLongitude, double maxDistanceKM, List<Product> filteredProducts, List<ProductResponse> filteredResponses) {
    ProductResponse response = ProductResponse.from(product);
    List<InventoryResponse> inventories = getProductInventories(response);

    if (inventories == null || inventories.isEmpty()) {
      return;
    }

    List<InventoryResponse> availableInventories = getAvailableInventories(
        inventories, userLatitude, userLongitude, maxDistanceKM);

    addProductIfHasNearbyInventory(product, response, availableInventories,
        userLatitude, userLongitude, filteredProducts, filteredResponses);
  }

  private List<InventoryResponse> getProductInventories(ProductResponse response) {
    return response.getProductVersionResponse().getInventories();
  }

  private List<InventoryResponse> getAvailableInventories(List<InventoryResponse> inventories, double userLatitude, double userLongitude, double maxDistanceKM) {
    return inventories.stream()
        .filter(inv -> inv.getDeletedAt() == null)
        .filter(inv -> withinRange(userLatitude, userLongitude,
            inv.getWarehouseLatitude(),
            inv.getWarehouseLongitude(),
            maxDistanceKM))
        .toList();
  }

  private void addProductIfHasNearbyInventory(Product product, ProductResponse response, List<InventoryResponse> availableInventories, double userLatitude, double userLongitude, List<Product> filteredProducts, List<ProductResponse> filteredResponses) {
    if (availableInventories.isEmpty()) {
      return;
    }

    Optional<InventoryResponse> nearestInventory = findNearestInventory(
        availableInventories, userLatitude, userLongitude);

    if (nearestInventory.isPresent()) {
      response.getProductVersionResponse().setInventories(List.of(nearestInventory.get()));
      filteredProducts.add(product);
      filteredResponses.add(response);
    }
  }

  private Optional<InventoryResponse> findNearestInventory(List<InventoryResponse> inventories, double userLatitude, double userLongitude) {
    return inventories.stream()
        .min((inv1, inv2) -> {
          double distance1 = calculateDistance(userLatitude, userLongitude, inv1);
          double distance2 = calculateDistance(userLatitude, userLongitude, inv2);
          return Double.compare(distance1, distance2);
        });
  }

  private double calculateDistance(double userLatitude, double userLongitude, InventoryResponse inventory) {
    return haversine(userLatitude, userLongitude,
        inventory.getWarehouseLatitude(),
        inventory.getWarehouseLongitude());
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