package com.pwdk.grocereach.shipping.applications.impl;

import com.pwdk.grocereach.shipping.applications.services.RajaOngkirService;
import com.pwdk.grocereach.shipping.domain.entities.ShippingCost;
import com.pwdk.grocereach.shipping.presentations.dto.rajaongkir.KomerceApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RajaOngkirServiceImpl implements RajaOngkirService {

    private final RestTemplate restTemplate;

    @Value("${rajaongkir.api.key}")
    private String apiKey;

    @Value("${rajaongkir.api.url:https://rajaongkir.komerce.id/api/v1}")
    private String apiUrl;

    private static final String[] SUPPORTED_COURIERS = {"jne", "jnt", "sicepat", "pos", "tiki"};

    @Override
    public List<ShippingCost> fetchShippingCosts(Integer originCityId, Integer destinationCityId, Integer weight) {
        List<ShippingCost> allCosts = new ArrayList<>();

        for (String courier : SUPPORTED_COURIERS) {
            try {
                List<ShippingCost> courierCosts = fetchCourierCosts(originCityId, destinationCityId, weight, courier);
                allCosts.addAll(courierCosts);
            } catch (Exception e) {
                log.warn("Failed to fetch costs for courier {}: {}", courier, e.getMessage());
            }
        }

        return allCosts;
    }

    @Override
    public List<ShippingCost> fetchCourierCosts(Integer originCityId, Integer destinationCityId,
                                                Integer weight, String courier) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("key", apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("origin", originCityId.toString());
        body.add("destination", destinationCityId.toString());
        body.add("weight", weight.toString());
        body.add("courier", courier);
        body.add("price", "lowest");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KomerceApiResponse> response = restTemplate.postForEntity(
                    apiUrl + "/calculate/domestic-cost", request, KomerceApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapToShippingCosts(response.getBody(), originCityId, destinationCityId, weight, courier);
            }
        } catch (Exception e) {
            log.error("Error calling RajaOngkir API for courier {}: {}", courier, e.getMessage());
            throw new RuntimeException("Failed to fetch shipping costs from RajaOngkir", e);
        }

        return new ArrayList<>();
    }

    private List<ShippingCost> mapToShippingCosts(KomerceApiResponse response, Integer originCityId,
                                                  Integer destinationCityId, Integer weight, String courier) {
        List<ShippingCost> costs = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        if (response.getMeta().getStatus().equals("success") && response.getData() != null) {
            response.getData().forEach(shippingData -> {
                ShippingCost shippingCost = ShippingCost.builder()
                        .originCityId(originCityId)
                        .destinationCityId(destinationCityId)
                        .courier(shippingData.getCode().toLowerCase())
                        .service(shippingData.getService())
                        .serviceName(shippingData.getDescription())
                        .cost(shippingData.getCost())
                        .etd(shippingData.getEtd())
                        .weight(weight)
                        .description(shippingData.getDescription())
                        .isActive(true)
                        .lastUpdatedFromApi(now)
                        .build();
                costs.add(shippingCost);
            });
        }

        return costs;
    }

}