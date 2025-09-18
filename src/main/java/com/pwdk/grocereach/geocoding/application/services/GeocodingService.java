package com.pwdk.grocereach.geocoding.application.services;

import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingDTOs;
import java.util.Optional;

public interface GeocodingService {
    /**
     * Converts a human-readable address into geographic coordinates.
     * @param address The address to geocode.
     * @return An Optional containing the Coordinates if found, otherwise empty.
     */
    Optional<GeocodingDTOs.Coordinates> geocode(String address);

    /**
     * Converts geographic coordinates into a human-readable address.
     * @param lat The latitude.
     * @param lng The longitude.
     * @return An Optional containing the formatted address string if found, otherwise empty.
     */
    Optional<String> reverseGeocode(double lat, double lng);
}