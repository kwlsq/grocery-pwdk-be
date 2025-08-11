package com.pwdk.grocereach.store.infrastructures.repositories;

import com.pwdk.grocereach.store.domains.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
}
