package com.pwdk.grocereach.cart.infrastructure.repository;

import com.pwdk.grocereach.cart.domain.entities.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartItems, UUID>, JpaSpecificationExecutor<CartItems> {
    List<CartItems> findAllByUserIdAndDeletedAtIsNull(UUID userId);

    boolean existsByUserIdAndProductIdAndDeletedAtIsNull(UUID userId, UUID productId);

    void deleteAllByIdIn(List<UUID> ids);
}
