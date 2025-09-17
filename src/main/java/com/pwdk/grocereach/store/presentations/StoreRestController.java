package com.pwdk.grocereach.store.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.common.exception.StoreNotFoundException;
import com.pwdk.grocereach.store.applications.StoreServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store")
public class StoreRestController {

  private final StoreServices storeServices;

  public StoreRestController(StoreServices storeServices) {
    this.storeServices = storeServices;
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllStores() {
    return Response.successfulResponse(
        "Successfully get all stores!",
        storeServices.getAllStores()
    );
  }

  @GetMapping
  public ResponseEntity<?> getStoreByUser(Authentication authentication) {

    String uuid = authentication.getName();

    try {
      return Response.successfulResponse(
          "Successfully get store for user!",
          storeServices.getStoreByUser(uuid)
      );
    } catch (StoreNotFoundException e) {
      return Response.failedResponse(
          "No store found for this user"
      );
    }
  }
}
