package com.pwdk.grocereach.order.infrastructures.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pwdk.grocereach.order.domains.entities.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, UUID> {
}


