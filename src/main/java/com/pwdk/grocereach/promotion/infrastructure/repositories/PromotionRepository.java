package com.pwdk.grocereach.promotion.infrastructure.repositories;

import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotions, UUID>, JpaSpecificationExecutor<Promotions> {
}
