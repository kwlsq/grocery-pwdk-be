package com.pwdk.grocereach.shipping.applications.services;

import com.pwdk.grocereach.shipping.domain.entities.ShippingCost;

import java.util.List;

public interface RajaOngkirService {

    List<ShippingCost> fetchShippingCosts(Integer originCityId, Integer destinationCityId, Integer weight);
    List<ShippingCost> fetchCourierCosts(Integer originCityId, Integer destinationCityId,
                                         Integer weight, String courier);
}