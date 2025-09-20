package com.pwdk.grocereach.store.infrastructures.repositories;

import com.pwdk.grocereach.store.domains.entities.Stores;
import com.pwdk.grocereach.store.domains.entities.Warehouse;
import com.pwdk.grocereach.store.presentations.dtos.UniqueWarehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
  Page<Warehouse> findAllByStore_Id(UUID storeId, Pageable pageable);
  @Query("""
        SELECT w.id AS id, w.name AS name
        FROM Warehouse w
        WHERE w.store.id = :storeId
    """)
  List<UniqueWarehouse> findAllByStoreId(UUID storeId);

  UUID store(Stores store);
}
