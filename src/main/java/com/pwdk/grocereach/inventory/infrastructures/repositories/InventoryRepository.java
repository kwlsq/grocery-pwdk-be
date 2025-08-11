package com.pwdk.grocereach.inventory.infrastructures.repositories;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
}
