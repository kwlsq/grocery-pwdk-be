package com.pwdk.grocereach.product.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.product.applications.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        "Product fetched successfully",
        productService.getAllProducts(pageable, search, category)
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
