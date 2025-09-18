package com.pwdk.grocereach.geocoding.application.impl;

import com.pwdk.grocereach.geocoding.application.services.GeocodingService;
import com.pwdk.grocereach.geocoding.infrastructure.config.GoogleGeocodingConfig;
import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingDTOs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {

    private final RestTemplate restTemplate;
    private final GoogleGeocodingConfig googleConfig;
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    @Override
    public Optional<GeocodingDTOs.Coordinates> geocode(String address) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE_API_URL)
                .queryParam("address", address)
                .queryParam("key", googleConfig.getApiKey())
                .queryParam("components", "country:ID"); // Bias results to Indonesia

        GeocodingDTOs.Google.Response response = restTemplate.getForObject(builder.toUriString(), GeocodingDTOs.Google.Response.class);

        if (response != null && "OK".equals(response.getStatus()) && !response.getResults().isEmpty()) {
            return Optional.ofNullable(response.getResults().get(0).getGeometry().getLocation());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> reverseGeocode(double lat, double lng) {
        String latlng = String.format("%f,%f", lat, lng);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE_API_URL)
                .queryParam("latlng", latlng)
                .queryParam("key", googleConfig.getApiKey());

        GeocodingDTOs.Google.Response response = restTemplate.getForObject(builder.toUriString(), GeocodingDTOs.Google.Response.class);

        if (response != null && "OK".equals(response.getStatus()) && !response.getResults().isEmpty()) {
            // Return the formatted address from the first result
            return Optional.ofNullable(response.getResults().get(0).getFormattedAddress());
        }
        return Optional.empty();
    }
}

