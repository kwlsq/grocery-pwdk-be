package com.pwdk.grocereach.shipping.infrastructure.repositories;

import com.pwdk.grocereach.shipping.domain.entities.ShippingCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShippingCostRepository extends JpaRepository<ShippingCost, UUID> {

    List<ShippingCost> findByOriginCityIdAndDestinationCityIdAndWeightAndCourierAndIsActiveTrue(
            Integer originCityId, Integer destinationCityId, Integer weight, String courier);

    void deleteByOriginCityIdAndDestinationCityIdAndWeight(
            Integer originCityId, Integer destinationCityId, Integer weight);

    List<ShippingCost> findByOriginCityIdAndDestinationCityIdAndWeightAndIsActiveTrue(
            Integer originCityId, Integer destinationCityId, Integer weight);
}