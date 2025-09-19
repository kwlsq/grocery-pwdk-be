package com.pwdk.grocereach.geocoding.presentation.controllers;

import com.pwdk.grocereach.geocoding.application.services.GeocodingService;
import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingRequest;
import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingResponse;
import com.pwdk.grocereach.geocoding.presentation.dtos.PlacesAutocompleteResponse;
import com.pwdk.grocereach.geocoding.presentation.dtos.ReverseGeocodingRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/api/v1/geocoding")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class GeocodingController {

    @Qualifier("googleGeocodingService")
    private final GeocodingService geocodingService;

    @PostMapping
    public ResponseEntity<GeocodingResponse> geocode(@Valid @RequestBody GeocodingRequest request) {
        log.info("Received geocoding request for address: {} using provider: {}",
                request.getAddress(), geocodingService.getProviderName());

        GeocodingResponse response = geocodingService.geocode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<GeocodingResponse> geocodeGet(
            @RequestParam @NotBlank(message = "Address parameter is required") String address,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String countryCode) {

        log.info("Received GET geocoding request for address: {} using provider: {}",
                address, geocodingService.getProviderName());

        GeocodingRequest request = GeocodingRequest.builder()
                .address(address)
                .language(language)  // Will use "id" as default if null/empty
                .region(region)      // Will use "id" as default if null/empty
                .countryCode(countryCode)
                .build();

        GeocodingResponse response = geocodingService.geocode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        String providerInfo = String.format("Geocoding service is running using %s provider!",
                geocodingService.getProviderName());
        return ResponseEntity.ok(new HealthResponse(providerInfo, "UP"));
    }

    @GetMapping("/provider")
    public ResponseEntity<ProviderResponse> getProvider() {
        return ResponseEntity.ok(new ProviderResponse(geocodingService.getProviderName()));
    }
    @Data
    @AllArgsConstructor
    public static class HealthResponse {
        private String message;
        private String status;
    }

    @Data
    @AllArgsConstructor
    public static class ProviderResponse {
        private String provider;
    }

    @GetMapping("/debug")
    public ResponseEntity<DebugResponse> debugUrl(
            @RequestParam @NotBlank(message = "Address parameter is required") String address,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String countryCode) {

        log.info("Debug request for address: {}", address);

        GeocodingRequest request = GeocodingRequest.builder()
                .address(address)
                .language(language)
                .region(region)
                .countryCode(countryCode)
                .build();
        if (geocodingService instanceof com.pwdk.grocereach.geocoding.application.impl.GoogleGeocodingServiceImpl) {
            var impl = (com.pwdk.grocereach.geocoding.application.impl.GoogleGeocodingServiceImpl) geocodingService;
            String builtUrl = impl.buildUrlForDebug(request);

            return ResponseEntity.ok(new DebugResponse(
                    request.getAddress(),
                    builtUrl,
                    "Copy this URL to test directly in browser/Postman"
            ));
        }

        return ResponseEntity.ok(new DebugResponse(
                request.getAddress(),
                "Debug not available",
                "Service implementation not found"
        ));
    }
@Data
@AllArgsConstructor
public static class DebugResponse {
    private String originalAddress;
    private String generatedUrl;
    private String instructions;
}
    @GetMapping("/places/autocomplete")
    public ResponseEntity<PlacesAutocompleteResponse> placesAutocomplete(
            @RequestParam @NotBlank String input,
            @RequestParam(required = false, defaultValue = "id") String language,
            @RequestParam(required = false) String sessionToken) {

        log.info("Places autocomplete request for: {}", input);

        // Call your service (we'll create this)
        PlacesAutocompleteResponse response = geocodingService.getPlacesAutocomplete(input, language, sessionToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reverse")
    public ResponseEntity<GeocodingResponse> reverseGeocode(@Valid @RequestBody ReverseGeocodingRequest request) {
        log.info("Reverse geocoding request for: {}, {}", request.getLat(), request.getLng());

        GeocodingResponse response = geocodingService.reverseGeocode(request);
        return ResponseEntity.ok(response);
    }
}
