package com.pwdk.grocereach.geocoding.presentation.controllers;

import com.pwdk.grocereach.geocoding.application.services.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/geocode")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;
    @GetMapping
    public ResponseEntity<?> geocodeAddress(@RequestParam String address) {
        return geocodingService.geocode(address)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/reverse")
    public ResponseEntity<?> reverseGeocodeAddress(@RequestParam double lat, @RequestParam double lng) {
        return geocodingService.reverseGeocode(lat, lng)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
