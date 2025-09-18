package com.pwdk.grocereach.geocoding.presentation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * A container class for all DTOs related to Geocoding API responses.
 */
public class GeocodingDTOs {

    /**
     * A simple class to represent the final latitude and longitude coordinates.
     */
    @Data
    @NoArgsConstructor
    public static class Coordinates {
        private double lat;
        private double lng;
    }

    /**
     * DTOs to map the JSON response from the Google Geocoding API.
     * @JsonIgnoreProperties allows us to ignore the many fields in the JSON
     * that we don't need in our application.
     */
    public static class Google {
        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Response {
            private List<Result> results;
            private String status;
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Result {
            // This annotation maps the JSON key "formatted_address" to our Java field "formattedAddress"
            @JsonProperty("formatted_address")
            private String formattedAddress;
            private Geometry geometry;
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Geometry {
            private Coordinates location;
        }
    }
}
