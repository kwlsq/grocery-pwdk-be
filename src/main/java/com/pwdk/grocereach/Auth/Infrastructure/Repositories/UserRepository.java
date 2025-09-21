package com.pwdk.grocereach.Auth.Infrastructure.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isVerified = true AND u.deletedAt IS NULL")
    Page<User> findAllVerifiedByRole(@Param("role") UserRole role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isVerified = true AND u.deletedAt IS NULL")
    Page<User> findAllByVerifiedTrue(Pageable pageable);

    // Note: search endpoints removed for now; add back when service supports searching
}
