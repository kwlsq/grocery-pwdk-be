package com.pwdk.grocereach.store.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.store.applications.WarehouseServices;
import com.pwdk.grocereach.store.presentations.dtos.CreateWarehouseRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseRestController {

  private final WarehouseServices warehouseServices;

  public WarehouseRestController(WarehouseServices warehouseServices) {
    this.warehouseServices = warehouseServices;
  }

  @GetMapping("/store/{storeID}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<?> getAllWarehouse(@PathVariable String storeID,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    UUID uuid = UUID.fromString(storeID);
    return Response.successfulResponse(
        "Successfully retrieve all warehouse!",
        warehouseServices.getAllOwnedWarehouse(uuid, pageable)
    );
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createWarehouse(@RequestBody CreateWarehouseRequest request) {
    return Response.successfulResponse(
        "Successfully create warehouse",
        warehouseServices.createWarehouse(request)
    );
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<?> getWarehouseByID(@PathVariable String id){
    return Response.successfulResponse(
        "Successfully retrieve all warehouse!",
        warehouseServices.getWarehouseByID(id)
    );
  }
}
