package com.pwdk.grocereach.store.presentations;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.common.exception.StoreNotFoundException;
import com.pwdk.grocereach.store.applications.StoreServices;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store")
public class StoreRestController {

  private final StoreServices storeServices;

  public StoreRestController(StoreServices storeServices) {
    this.storeServices = storeServices;
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllStores(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                        @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                        @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                        @RequestParam(value = "search", defaultValue = "") String search){

    Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sortBy, sortDirection)));

    return Response.successfulResponse(
        "Successfully get all stores!",
        storeServices.getAllStores(pageable, search)
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

  private Sort.Order getSortOrder(String sortBy, String sortDirection) {
    return Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection));
  }
}
