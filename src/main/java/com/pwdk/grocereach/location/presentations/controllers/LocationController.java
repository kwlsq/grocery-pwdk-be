package com.pwdk.grocereach.location.presentations.controllers;

import com.pwdk.grocereach.location.applications.services.LocationService;
import com.pwdk.grocereach.location.presentations.dtos.CityResponse;
import com.pwdk.grocereach.location.presentations.dtos.ProvinceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/provinces")
    public ResponseEntity<List<ProvinceResponse>> getAllProvinces() {
        return ResponseEntity.ok(locationService.getAllProvinces());
    }

    @GetMapping("/cities/{provinceId}")
    public ResponseEntity<List<CityResponse>> getCitiesByProvince(@PathVariable Integer provinceId) {
        return ResponseEntity.ok(locationService.getCitiesByProvince(provinceId));
    }
}
