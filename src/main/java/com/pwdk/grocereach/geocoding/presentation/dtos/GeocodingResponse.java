package com.pwdk.grocereach.geocoding.presentation.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeocodingResponse {
    private String status;
    private List<Result> results;

    @JsonProperty("processing_time_ms")
    private Long processingTimeMs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {
        @JsonProperty("formatted_address")
        private String formattedAddress;

        private Geometry geometry;

        @JsonProperty("place_id")
        private String placeId;

        private List<String> types;

        @JsonProperty("address_components")
        private List<AddressComponent> addressComponents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Geometry {
        private Location location;

        @JsonProperty("location_type")
        private String locationType;

        private Viewport viewport;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double lat;
        private Double lng;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Viewport {
        private Location northeast;
        private Location southwest;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressComponent {
        @JsonProperty("long_name")
        private String longName;

        @JsonProperty("short_name")
        private String shortName;

        private List<String> types;
    }
}
