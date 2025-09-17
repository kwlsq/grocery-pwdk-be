package com.pwdk.grocereach.store.infrastructures.repositories;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.store.domains.entities.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoresRepository extends JpaRepository<Stores, UUID>, JpaSpecificationExecutor<Stores> {
  Optional<Stores> findStoresByAdmin(User admin);
}
