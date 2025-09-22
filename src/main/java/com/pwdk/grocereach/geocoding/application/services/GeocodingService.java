package com.pwdk.grocereach.geocoding.application.services;

import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingRequest;
import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingResponse;
import com.pwdk.grocereach.geocoding.presentation.dtos.PlacesAutocompleteResponse;
import com.pwdk.grocereach.geocoding.presentation.dtos.ReverseGeocodingRequest;



public interface GeocodingService {
    GeocodingResponse geocode(GeocodingRequest request);
    GeocodingResponse reverseGeocode(ReverseGeocodingRequest request);
    PlacesAutocompleteResponse getPlacesAutocomplete(String input, String language, String sessionToken);
    String getProviderName();
}