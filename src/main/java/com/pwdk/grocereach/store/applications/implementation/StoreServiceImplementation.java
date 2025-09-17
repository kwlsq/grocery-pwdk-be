package com.pwdk.grocereach.store.applications.implementation;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.common.exception.StoreNotFoundException;
import com.pwdk.grocereach.common.exception.UserNotFoundException;
import com.pwdk.grocereach.store.applications.StoreServices;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.infrastructures.repositories.StoresRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.impl.StoreRepoImpl;
import com.pwdk.grocereach.store.presentations.dtos.StoreResponse;
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
  public List<StoreResponse> getAllStores() {
    return storesRepository.findAll().stream()
        .map(StoreResponse::from)
        .toList();
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
