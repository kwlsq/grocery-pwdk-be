package com.pwdk.grocereach.order.presentations;

import java.time.YearMonth;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.order.applications.SalesReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sales-reports")
@RequiredArgsConstructor
public class SalesReportController {

  private final SalesReportService salesReportService;

  private UUID resolveScopedStoreId(UUID requestedStoreId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    if (isAdmin) return requestedStoreId; // Admin can see any store
    boolean isManager = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
    if (isManager) {
      // TODO: derive manager's storeId from principal
      return requestedStoreId;
    }
    return requestedStoreId;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> getSalesReport(
      @RequestParam(required = false) UUID storeId,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) UUID productId,
      @RequestParam(required = false) String startMonth,
      @RequestParam(required = false) String endMonth,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    UUID scoped = resolveScopedStoreId(storeId);
    YearMonth now = YearMonth.now();
    YearMonth start = (startMonth == null || startMonth.isBlank()) ? YearMonth.of(now.getYear(), 1) : YearMonth.parse(startMonth);
    YearMonth end = (endMonth == null || endMonth.isBlank()) ? now : YearMonth.parse(endMonth);
    Pageable pageable = PageRequest.of(page, size);

    var data = salesReportService.getOrderHistoryReport(scoped, categoryId, productId, start, end, pageable);
    return Response.successfulResponse("Sales report fetched", data);
  }

  @GetMapping("/monthly-count")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> getMonthlyCount() {
    return Response.successfulResponse(
        "Successfully get monthly sales count!",
        salesReportService.getMonthlyCount()
    );
  }
}


