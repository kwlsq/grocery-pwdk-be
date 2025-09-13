package com.pwdk.grocereach.User.Infrastructure.Repositories; // Or your correct repositories package

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.User.Domain.Entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUser(User user);
    Optional<Address> findByIdAndUser(UUID id, User user);
    Optional<Address> findByUserAndIsPrimaryTrue(User user);
    @Modifying
    @Query("UPDATE Address a SET a.isPrimary = false WHERE a.user = :user AND a.isPrimary = true")
    void unsetAllPrimaryAddressesForUser(@Param("user") User user);
}