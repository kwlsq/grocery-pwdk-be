package com.pwdk.grocereach.cart.infrastructure.repository;

import com.pwdk.grocereach.cart.domain.entities.CartItems;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.product.domains.entities.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartItems, UUID>, JpaSpecificationExecutor<CartItems> {
    List<CartItems> findAllByUserAndDeletedAtIsNull(User user);

    boolean existsByUserAndProductAndDeletedAtIsNull(User user, Product product);

    void deleteAllByIdIn(List<UUID> ids);
}
