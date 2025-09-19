package com.pwdk.grocereach.product.presentations;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.common.exception.ProductAlreadyExistException;
import com.pwdk.grocereach.inventory.presentations.dtos.WarehouseStock;
import com.pwdk.grocereach.product.applications.ProductCategoryService;
import com.pwdk.grocereach.product.applications.ProductReadService;
import com.pwdk.grocereach.product.applications.ProductWriteService;
import com.pwdk.grocereach.product.presentations.dtos.CreateCategoryRequest;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import static com.pwdk.grocereach.store.presentations.StoreRestController.getDirection;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

  private final ProductReadService productReadService;
  private final ProductWriteService productWriteService;
  private final ProductCategoryService productCategoryService;

  public ProductRestController(ProductCategoryService productCategoryService, ProductWriteService productWriteService, ProductReadService productReadService) {
    this.productCategoryService = productCategoryService;
    this.productReadService = productReadService;
    this.productWriteService = productWriteService;
  }

  @GetMapping("/public")
  public ResponseEntity<?> getAllProducts(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                          @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                          @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                          @RequestParam(value = "search", defaultValue = "") String search,
                                          @RequestParam(value = "category", defaultValue = "") String category,
                                          @RequestParam(value = "userLatitude", defaultValue = "0") double userLatitude,
                                          @RequestParam(value = "userLongitude", defaultValue = "0") double userLongitude,
                                          @RequestParam(value = "maxDistanceKM", defaultValue = "10") double maxDistanceKM
                                          ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sortBy, sortDirection)));

    return Response.successfulResponse(
        "Products fetched successfully",
        productReadService.getAllProducts(pageable, search, category, userLatitude, userLongitude, maxDistanceKM)
    );
  }

  @GetMapping("/public/{id}")
  public ResponseEntity<?> getProductByID(@PathVariable String id) {
    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Product fetched successfully",
        productReadService.getProductByID(uuid)
    );
  }

  @GetMapping("/store/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<?> getProductsByStoreID(@PathVariable String id,
                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                                @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                                @RequestParam(value = "search", defaultValue = "") String search,
                                                @RequestParam(value = "category", defaultValue = "") String category) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sortBy, sortDirection)));

    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Product fetched successfully",
        productReadService.getProductsByStoreID(uuid, pageable, search, category)
    );
  }

  @GetMapping("/public/categories")
  public ResponseEntity<?> getAllCategories() {
    return Response.successfulResponse(
        "Categories fetched successfully!",
        productCategoryService.getAllCategories()
    );
  }

  @PostMapping()
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
    try {
      return Response.successfulResponse(
          "Product successfully created!",
          productWriteService.createProduct(request)
      );
    } catch (ProductAlreadyExistException e) {
      return Response.failedResponse(
          "Product with the same name already exist in this store!"
      );
    }
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateProduct(@PathVariable String id,@RequestBody UpdateProductRequest request) {
    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Update product successful",
        productWriteService.updateProduct(uuid, request)
    );
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteProduct(@PathVariable String id) {
    UUID uuid = UUID.fromString(id);
    productWriteService.deleteProduct(uuid);
    return Response.successfulResponse("Delete product success!");
  }

  @PatchMapping("/stocks/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateProductStock(@RequestBody List<WarehouseStock> inventories, @PathVariable String id) {
    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Successfully update product stock",
        productWriteService.updateProductStock(uuid, inventories)
    );
  }

  @PostMapping("/category")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request) {
    return Response.successfulResponse(
        "Successfully create new product category!",
        productCategoryService.createCategory(request)
    );
  }

  @DeleteMapping("/category/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteCategory(@PathVariable String id) {
    productCategoryService.deleteCategory(id);
    return Response.successfulResponse("Successfully delete product category!");
  }

  @GetMapping("/unique")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<?> getAllUniqueProduct() {
    return Response.successfulResponse(
        "Successfully fetched all unique product!",
        productReadService.getAllUniqueProduct()
    );
  }

  private Sort.Order getSortOrder(String sortBy, String sortDirection) {
    Sort.Direction direction = validateSortDirection(sortDirection);
    String mappedSortBy = mapSortBy(sortBy);
    return Sort.Order.by(mappedSortBy).with(direction);
  }

  private Sort.Direction validateSortDirection(String sortDirection) {
    return getDirection(sortDirection);
  }

  private String mapSortBy(String sortBy) {
    if (sortBy == null || sortBy.trim().isEmpty()) {
      return "id";
    }

    String normalized = sortBy.trim().toLowerCase();

    return switch (normalized) {
      case "price" -> "currentVersion.price";
      case "weight" -> "currentVersion.weight";
      case "name" -> "name";
      default -> sortBy;
    };
  }
}
