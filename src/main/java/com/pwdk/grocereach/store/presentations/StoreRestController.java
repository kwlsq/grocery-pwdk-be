package com.pwdk.grocereach.store.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.store.applications.StoreServices;
import com.pwdk.grocereach.store.presentations.dtos.AssignManagerRequest;
import com.pwdk.grocereach.store.presentations.dtos.StoreRequest;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<StoreResponse>> getAllStores() {
    return ResponseEntity.ok(storeService.getAllStores());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StoreResponse> getStoreById(@PathVariable UUID id) {
    return ResponseEntity.ok(storeService.getStoreById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StoreResponse> updateStore(@PathVariable UUID id, @Valid @RequestBody StoreRequest request) {
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
}
