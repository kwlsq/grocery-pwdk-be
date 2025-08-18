package com.pwdk.grocereach.product.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.product.applications.ProductService;
import com.pwdk.grocereach.product.presentations.dtos.CreateProductRequest;
import com.pwdk.grocereach.product.presentations.dtos.UpdateProductRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

  private final ProductService productService;

  public ProductRestController(ProductService productService) {
    this.productService = productService;
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
        productService.getAllProducts(pageable, search, category, userLatitude, userLongitude, maxDistanceKM)
    );
  }

  @GetMapping("/public/{id}")
  public ResponseEntity<?> getProductByID(@PathVariable String id) {
    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Product fetched successfully",
        productService.getProductByID(uuid)
    );
  }

  @GetMapping("/admin/{id}")
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
        productService.getProductsByStoreID(uuid, pageable, search, category)
    );
  }

  @GetMapping("/public/categories")
  public ResponseEntity<?> getAllCategories() {
    return Response.successfulResponse(
        "Categories fetched successfully!",
        productService.getAllCategories()
    );
  }

  @PostMapping("/create")
  public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
    return Response.successfulResponse(
        "Product successfully created!",
        productService.createProduct(request)
    );
  }

  @PatchMapping("/update/{id}")
  public ResponseEntity<?> updateProduct(@PathVariable String id,@RequestBody UpdateProductRequest request) {
    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Update product successful",
        productService.updateProduct(uuid, request)
    );
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable String id) {
    UUID uuid = UUID.fromString(id);
    productService.deleteProduct(uuid);
    return Response.successfulResponse("Delete product success!");
  }

  private Sort.Order getSortOrder(String sortBy, String sortDirection) {
    return Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection));
  }
}
