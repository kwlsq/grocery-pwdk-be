package com.pwdk.grocereach.store.infrastructures.repositories;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.presentations.dtos.UniqueStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoresRepository extends JpaRepository<Stores, UUID>, JpaSpecificationExecutor<Stores> {
  Optional<Stores> findStoresByAdmin(User admin);
  @Query("SELECT DISTINCT s.id as id, s.storeName as name FROM Stores s ")
  List<UniqueStore> findAllUniqueStore();
}
