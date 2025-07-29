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
@RequestMapping("api/v1/products")
public class ProductRestController {

  private final ProductService productService;

  public ProductRestController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<?> getAllProducts(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                          @RequestParam(value = "sort", defaultValue = "id") String sort,
                                          @RequestParam(value = "search", defaultValue = "") String search,
                                          @RequestParam(value = "category", defaultValue = "") Integer category
                                          ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sort)));

    return Response.successfulResponse(
        "Products fetched successfully",
        productService.getAllProducts(pageable, search, category)
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProductByID(@PathVariable String id) {
    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Product fetched successfully",
        productService.getProductByID(uuid)
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
  public ResponseEntity<?> updateProduct(@PathVariable String id, UpdateProductRequest request) {
    UUID uuid = UUID.fromString(id);
    return Response.successfulResponse(
        "Update product successful",
        productService.updateProduct(uuid, request)
    );
  }



  private Sort.Order getSortOrder(String sort) {
    String[] sortParts = sort.split(",");
    String property = sortParts[0];
    Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
    return Sort.Order.by(property).with(direction);
  }
}
