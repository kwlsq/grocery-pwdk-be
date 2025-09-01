package com.pwdk.grocereach.promotion.presentation;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.promotion.application.PromotionService;
import com.pwdk.grocereach.promotion.presentation.dto.AttachPromotionRequest;
import com.pwdk.grocereach.promotion.presentation.dto.CreatePromotionRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionRestController {

  private final PromotionService promotionService;

  public PromotionRestController(PromotionService promotionService) {
    this.promotionService = promotionService;
  }

  @GetMapping
  public ResponseEntity<?> getAllPromotions(@RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "12") int size) {

    Pageable pageable = PageRequest.of(page, size);

    return Response.successfulResponse(
        "Successfully fetched all promotions",
        promotionService.getAllPromotions(pageable)
    );
  }

  @PostMapping
  public ResponseEntity<?> createPromotion(@RequestBody CreatePromotionRequest request) {
    return Response.successfulResponse(
        "Successfully create promotion",
        promotionService.createPromotion(request)
    );
  }
}
