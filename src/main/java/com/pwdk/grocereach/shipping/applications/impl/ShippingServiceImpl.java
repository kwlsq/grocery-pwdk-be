package com.pwdk.grocereach.shipping.applications.impl;

import com.pwdk.grocereach.User.Application.Services.AddressService;
import com.pwdk.grocereach.User.Domain.Entities.Address;
import com.pwdk.grocereach.User.Infrastructure.Repositories.AddressRepository;
import com.pwdk.grocereach.User.Presentation.Dto.AddressResponse;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.shipping.applications.services.ShippingService;
import com.pwdk.grocereach.shipping.applications.services.RajaOngkirService;
import com.pwdk.grocereach.shipping.domain.entities.ShippingCost;
import com.pwdk.grocereach.shipping.infrastructure.repositories.ShippingCostRepository;
import com.pwdk.grocereach.shipping.presentations.dto.CheckoutAddressResponse;
import com.pwdk.grocereach.shipping.presentations.dto.ShippingCalculationRequest;
import com.pwdk.grocereach.shipping.presentations.dto.ShippingOptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService {

    private final ShippingCostRepository shippingCostRepository;
    private final RajaOngkirService rajaOngkirService;
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private static final int CACHE_EXPIRY_HOURS = 24;

    @Override
    public CheckoutAddressResponse getUserAddressesForCheckout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AddressResponse> addresses = addressService.getUserAddresses(user.getEmail());

        AddressResponse primaryAddress = addresses.stream()
                .filter(AddressResponse::isPrimary)
                .findFirst()
                .orElse(addresses.isEmpty() ? null : addresses.get(0));

        return CheckoutAddressResponse.builder()
                .addresses(addresses)
                .primaryAddress(primaryAddress)
                .hasAddresses(!addresses.isEmpty())
                .build();
    }

    @Override
    @Transactional
    public List<ShippingOptionResponse> calculateShippingOptions(ShippingCalculationRequest request) {
        AddressResponse address = getAddressById(request.getAddressId());

        Integer originCityId = getStoreCityId(request.getStoreId());
        Integer destinationCityId = address.getCityId();
        Integer weight = request.getTotalWeight();

        log.info("Calculating shipping from city {} to city {} for weight {}",
                originCityId, destinationCityId, weight);

        List<ShippingCost> cachedCosts = getCachedShippingCosts(originCityId, destinationCityId, weight);

        if (cachedCosts.isEmpty() || isExpired(cachedCosts)) {
            log.info("Fetching fresh shipping costs from API");

            clearExpiredCache(originCityId, destinationCityId, weight);
            List<ShippingCost> freshCosts = rajaOngkirService.fetchShippingCosts(originCityId, destinationCityId, weight);
            cachedCosts = shippingCostRepository.saveAll(freshCosts);
        } else {
            log.info("Using cached shipping costs, found {} options", cachedCosts.size());
        }

        return cachedCosts.stream()
                .map(this::mapToShippingOptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ShippingOptionResponse getShippingCost(UUID storeId, UUID addressId, Integer weight,
                                                  String courier, String service) {
        AddressResponse address = getAddressById(addressId);
        Integer originCityId = getStoreCityId(storeId);

        List<ShippingCost> costs = shippingCostRepository
                .findByOriginCityIdAndDestinationCityIdAndWeightAndCourierAndIsActiveTrue(
                        originCityId, address.getCityId(), weight, courier);

        return costs.stream()
                .filter(cost -> cost.getService().equals(service))
                .findFirst()
                .map(this::mapToShippingOptionResponse)
                .orElseThrow(() -> new RuntimeException("Shipping option not found"));
    }

    private AddressResponse getAddressById(UUID addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));
        return new AddressResponse(address);
    }

    private Integer getStoreCityId(UUID storeId) {
        log.info("Using default store city ID (Bandung: 23) for storeId: {}", storeId);
        return 23;
    }

    private List<ShippingCost> getCachedShippingCosts(Integer originCityId, Integer destinationCityId, Integer weight) {
        return shippingCostRepository.findByOriginCityIdAndDestinationCityIdAndWeightAndIsActiveTrue(
                originCityId, destinationCityId, weight);
    }

    private boolean isExpired(List<ShippingCost> costs) {
        return costs.stream()
                .anyMatch(cost -> cost.getLastUpdatedFromApi() == null ||
                        cost.getLastUpdatedFromApi().isBefore(LocalDateTime.now().minusHours(CACHE_EXPIRY_HOURS)));
    }

    private void clearExpiredCache(Integer originCityId, Integer destinationCityId, Integer weight) {
        shippingCostRepository.deleteByOriginCityIdAndDestinationCityIdAndWeight(
                originCityId, destinationCityId, weight);
    }

    private ShippingOptionResponse mapToShippingOptionResponse(ShippingCost shippingCost) {
        return ShippingOptionResponse.builder()
                .courier(shippingCost.getCourier())
                .service(shippingCost.getService())
                .serviceName(shippingCost.getServiceName())
                .cost(shippingCost.getCost())
                .etd(shippingCost.getEtd())
                .description(shippingCost.getDescription())
                .build();
    }
}