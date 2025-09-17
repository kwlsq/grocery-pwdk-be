package com.pwdk.grocereach.store.applications.implementation;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.common.exception.StoreNotFoundException;
import com.pwdk.grocereach.common.exception.UserNotFoundException;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.product.infrastructures.specification.ProductSpecification;
import com.pwdk.grocereach.store.applications.StoreServices;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.StoresRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.StoreRepoImpl;
import com.pwdk.grocereach.store.infrastructures.specifications.StoreSpecification;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoreServiceImplementation implements StoreServices {

  private final StoresRepository storesRepository;
  private final UserRepository userRepository;
  private final StoreRepoImpl storeRepoImpl;

  public StoreServiceImplementation(StoresRepository storesRepository, UserRepository userRepository, StoreRepoImpl storeRepoImpl) {
    this.storesRepository = storesRepository;
    this.storeRepoImpl = storeRepoImpl;
    this.userRepository = userRepository;
  }

  @Override
  public PaginatedResponse<StoreResponse> getAllStores(Pageable pageable, String search) {

    Page<Stores> page = storesRepository.findAll(StoreSpecification.getFilteredStore(search), pageable);

    List<StoreResponse> storeResponses = page.getContent().stream()
        .map(StoreResponse::from)
        .toList();

    return PaginatedResponse.Utils.from(page, storeResponses);
  }

  @Override
  public StoreResponse getStoreByUser(String uuid) {
    UUID userID = UUID.fromString(uuid);

    User user = userRepository.findById(userID).orElseThrow(() -> new UserNotFoundException("User not found!"));

    Optional<Stores> optionalStore = storeRepoImpl.findStoreByUser(user);

    if (optionalStore.isEmpty()) {
      throw new StoreNotFoundException("No store found for this user");
    }

    return StoreResponse.from(optionalStore.get());
  }
}
