package com.pwdk.grocereach.promotion.presentation;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.promotion.application.PromotionService;
import com.pwdk.grocereach.promotion.domain.enums.PromotionUnit;
import com.pwdk.grocereach.promotion.presentation.dto.CreatePromotionRequest;
import com.pwdk.grocereach.store.presentations.StoreRestController;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionRestController {

  private final PromotionService promotionService;

  public PromotionRestController(PromotionService promotionService) {
    this.promotionService = promotionService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<?> getAllPromotions(@RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "12") int size,
                                            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                            @RequestParam(value = "search", defaultValue = "") String search,
                                            @RequestParam(value = "unit", defaultValue = "") PromotionUnit unit) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sortBy, sortDirection)));

    return Response.successfulResponse(
        "Successfully fetched all promotions",
        promotionService.getAllPromotions(pageable, search, unit)
    );
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createPromotion(@RequestBody CreatePromotionRequest request) {
    return Response.successfulResponse(
        "Successfully create promotion",
        promotionService.createPromotion(request)
    );
  }

  private Sort.Order getSortOrder(String sortBy, String sortDirection) {
    return Sort.Order.by(sortBy).with(validateSortDirection(sortDirection));
  }

  private Sort.Direction validateSortDirection(String sortDirection) {
    return getDirection(sortDirection);
  }

  public static Sort.Direction getDirection(String sortDirection) {
    return StoreRestController.getDirection(sortDirection);
  }
}
