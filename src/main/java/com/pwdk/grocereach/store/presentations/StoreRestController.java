package com.pwdk.grocereach.store.presentations;
import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.common.exception.StoreNotFoundException;
import com.pwdk.grocereach.store.applications.StoreServices;
import com.pwdk.grocereach.store.presentations.dtos.AssignManagerRequest;
import com.pwdk.grocereach.store.presentations.dtos.StoreRequest;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import com.pwdk.grocereach.store.presentations.dtos.UpdateStoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreRestController {

  private final StoreServices storeService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest request) {
    return new ResponseEntity<>(storeService.createStore(request), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StoreResponse> getStoreById(@PathVariable UUID id) {
    return ResponseEntity.ok(storeService.getStoreById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StoreResponse> updateStore(@PathVariable UUID id, @Valid @RequestBody UpdateStoreRequest request) {
    return ResponseEntity.ok(storeService.updateStore(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteStore(@PathVariable UUID id) {
    storeService.deleteStore(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{storeId}/assign-manager")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StoreResponse> assignManager(@PathVariable UUID storeId, @Valid @RequestBody AssignManagerRequest request) {
    return ResponseEntity.ok(storeService.assignManagerToStore(storeId, request.getUserId()));
  }

  @DeleteMapping("/{storeId}/unassign-manager")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> unassignManager(@PathVariable UUID storeId) {
    try {
      StoreResponse response = storeService.unassignManagerFromStore(storeId);
      return Response.successfulResponse(
              "Manager successfully unassigned from store",
              response
      );
    } catch (StoreNotFoundException e) {
      return Response.failedResponse("Store not found: " + e.getMessage());
    } catch (IllegalStateException e) {
      return Response.failedResponse(e.getMessage());
    } catch (Exception e) {
      return Response.failedResponse("Failed to unassign manager: " + e.getMessage());
    }
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllStores(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                        @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                        @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                        @RequestParam(value = "search", defaultValue = "") String search) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sortBy, sortDirection)));

    return Response.successfulResponse(
            "Successfully get all stores!",
            storeService.getAllStores(pageable, search)
    );
  }

  @GetMapping
  public ResponseEntity<?> getStoreByUser(Authentication authentication) {

    String uuid = authentication.getName();

    try {
      return Response.successfulResponse(
              "Successfully get store for user!",
              storeService.getStoreByUser(uuid)
      );
    } catch (StoreNotFoundException e) {
      return Response.failedResponse(
              "No store found for this user"
      );
    }
  }

  private Sort.Order getSortOrder(String sortBy, String sortDirection) {
    return Sort.Order.by(sortBy).with(validateSortDirection(sortDirection));
  }

  private Sort.Direction validateSortDirection(String sortDirection) {
    return getDirection(sortDirection);
  }

  public static Sort.Direction getDirection(String sortDirection) {
    if (sortDirection == null || sortDirection.trim().isEmpty()) {
      return Sort.Direction.DESC; // default
    }

    String normalizedDirection = sortDirection.trim().toLowerCase();

    return switch (normalizedDirection) {
      case "asc", "ascending" -> Sort.Direction.ASC;
      case "desc", "descending" -> Sort.Direction.DESC;
      default -> throw new IllegalArgumentException("Invalid sort direction: " + sortDirection +
              ". Use 'asc' or 'desc'");
    };
  }

  @GetMapping("/unique")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<?> getAllUniqueProduct() {
    return Response.successfulResponse(
            "Successfully fetched all unique stores!",
            storeService.getAllUniqueStore()
    );
  }
}