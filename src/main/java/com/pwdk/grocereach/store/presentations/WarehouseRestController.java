package com.pwdk.grocereach.store.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.store.applications.WarehouseServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseRestController {

  private final WarehouseServices warehouseServices;

  public WarehouseRestController(WarehouseServices warehouseServices) {
    this.warehouseServices = warehouseServices;
  }

  @GetMapping("/{storeID}")
  public ResponseEntity<?> getAllWarehouse(@PathVariable String storeID) {
    UUID uuid = UUID.fromString(storeID);
    return Response.successfulResponse(
        "Successfully retrieve all warehouse!",
        warehouseServices.getAllOwnedWarehouse(uuid)
    );
  }
}
