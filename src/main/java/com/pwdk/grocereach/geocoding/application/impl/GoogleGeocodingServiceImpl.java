package com.pwdk.grocereach.geocoding.application.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwdk.grocereach.geocoding.application.services.GeocodingService;
import com.pwdk.grocereach.geocoding.infrastructure.config.GoogleGeocodingConfig;
import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingRequest;
import com.pwdk.grocereach.geocoding.presentation.dtos.GeocodingResponse;
import com.pwdk.grocereach.geocoding.presentation.dtos.PlacesAutocompleteResponse;
import com.pwdk.grocereach.geocoding.presentation.dtos.ReverseGeocodingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("googleGeocodingService")
@RequiredArgsConstructor
public class GoogleGeocodingServiceImpl implements GeocodingService {

    private final RestTemplate restTemplate;
    private final GoogleGeocodingConfig config;


    @Override
    public GeocodingResponse geocode(GeocodingRequest request) {
        long startTime = System.currentTimeMillis();

        log.info("Starting Google Maps geocoding for address: {}", request.getAddress());

        try {
            // Build URL with parameters
            String url = buildUrl(request);
            log.debug("Google Maps Request URL: {}", url);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(httpResponse.body());

            log.info("🔍 RAW RESPONSE: {}", httpResponse.body());
            // Process response
            GeocodingResponse result = processResponse(response);
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            log.info("Google Maps geocoding completed in {} ms with status: {}",
                    result.getProcessingTimeMs(), result.getStatus());
            return result;

        } catch (Exception e) {
            log.error("Error during Google Maps geocoding for address: {}", request.getAddress(), e);
            return GeocodingResponse.builder()
                    .status("ERROR: " + e.getMessage())
                    .results(new ArrayList<>())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public String getProviderName() {
        return "Google Maps";
    }

    private String buildUrl(GeocodingRequest request) {
        String baseUrl = config.getBaseUrl() != null ?
                config.getBaseUrl() : "https://maps.googleapis.com/maps/api/geocode/json";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("address", request.getAddress())
                .queryParam("key", config.getApiKey());

        // Always add language and region with defaults
        String language = request.getLanguage() != null && !request.getLanguage().trim().isEmpty()
                ? request.getLanguage() : "id";
        builder.queryParam("language", language);

        String region = request.getRegion() != null && !request.getRegion().trim().isEmpty()
                ? request.getRegion() : "id";
        builder.queryParam("region", region);

        if (request.getCountryCode() != null && !request.getCountryCode().trim().isEmpty()) {
            builder.queryParam("components", "country:" + request.getCountryCode().toUpperCase());
        }

        log.debug("Built URL with language={}, region={}", language, region);
        return builder.toUriString();
    }
    public String buildUrlForDebug(GeocodingRequest request) {
        return buildUrl(request);
    }

    private GeocodingResponse processResponse(JsonNode jsonResponse) {
        String status = jsonResponse.get("status").asText();

        if ("OK".equals(status)) {
            JsonNode resultsArray = jsonResponse.get("results");
            List<GeocodingResponse.Result> results = new ArrayList<>();

            for (JsonNode resultNode : resultsArray) {
                results.add(processResult(resultNode));
            }

            return GeocodingResponse.builder()
                    .status(status)
                    .results(results)
                    .build();
        } else {
            log.warn("Google Maps API returned status: {}", status);
            return GeocodingResponse.builder()
                    .status(status)
                    .results(new ArrayList<>())
                    .build();
        }
    }

    private GeocodingResponse.Result processResult(JsonNode resultNode) {
        return GeocodingResponse.Result.builder()
                .formattedAddress(resultNode.get("formatted_address").asText())
                .placeId(resultNode.get("place_id").asText())
                .types(extractTypes(resultNode.get("types")))
                .geometry(processGeometry(resultNode.get("geometry")))
                .addressComponents(processAddressComponents(resultNode.get("address_components")))
                .build();
    }

    private List<String> extractTypes(JsonNode typesArray) {
        List<String> types = new ArrayList<>();
        for (JsonNode type : typesArray) {
            types.add(type.asText());
        }
        return types;
    }

    private GeocodingResponse.Geometry processGeometry(JsonNode geometryNode) {
        JsonNode locationNode = geometryNode.get("location");
        GeocodingResponse.Location location = new GeocodingResponse.Location(
                locationNode.get("lat").asDouble(),
                locationNode.get("lng").asDouble()
        );

        GeocodingResponse.Viewport viewport = null;
        if (geometryNode.has("viewport")) {
            JsonNode viewportNode = geometryNode.get("viewport");
            JsonNode northeast = viewportNode.get("northeast");
            JsonNode southwest = viewportNode.get("southwest");

            viewport = GeocodingResponse.Viewport.builder()
                    .northeast(new GeocodingResponse.Location(
                            northeast.get("lat").asDouble(),
                            northeast.get("lng").asDouble()
                    ))
                    .southwest(new GeocodingResponse.Location(
                            southwest.get("lat").asDouble(),
                            southwest.get("lng").asDouble()
                    ))
                    .build();
        }

        return GeocodingResponse.Geometry.builder()
                .location(location)
                .locationType(geometryNode.get("location_type").asText())
                .viewport(viewport)
                .build();
    }

    private List<GeocodingResponse.AddressComponent> processAddressComponents(JsonNode addressComponentsArray) {
        List<GeocodingResponse.AddressComponent> addressComponents = new ArrayList<>();

        for (JsonNode componentNode : addressComponentsArray) {
            addressComponents.add(GeocodingResponse.AddressComponent.builder()
                    .longName(componentNode.get("long_name").asText())
                    .shortName(componentNode.get("short_name").asText())
                    .types(extractTypes(componentNode.get("types")))
                    .build());
        }

        return addressComponents;
    }
    @Override
    public GeocodingResponse reverseGeocode(ReverseGeocodingRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            String url = buildReverseGeocodeUrl(request);
            log.info("🔄 Reverse geocoding: {}, {}", request.getLat(), request.getLng());

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(httpResponse.body());

            GeocodingResponse result = processResponse(response);
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            return result;

        } catch (Exception e) {
            log.error("Error during reverse geocoding", e);
            return GeocodingResponse.builder()
                    .status("ERROR: " + e.getMessage())
                    .results(new ArrayList<>())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    private PlacesAutocompleteResponse processPlacesResponse(JsonNode response) {
        String status = response.get("status").asText();
        List<PlacesAutocompleteResponse.Prediction> predictions = new ArrayList<>();

        if ("OK".equals(status) && response.has("predictions")) {
            JsonNode predictionsArray = response.get("predictions");

            for (JsonNode predictionNode : predictionsArray) {
                PlacesAutocompleteResponse.Prediction prediction = new PlacesAutocompleteResponse.Prediction();

                prediction.setDescription(predictionNode.get("description").asText());
                prediction.setPlaceId(predictionNode.get("place_id").asText());

                // Process matched_substrings if present
                List<PlacesAutocompleteResponse.Prediction.MatchedSubstring> matchedSubstrings = new ArrayList<>();
                if (predictionNode.has("matched_substrings")) {
                    JsonNode matchedArray = predictionNode.get("matched_substrings");
                    for (JsonNode matchNode : matchedArray) {
                        PlacesAutocompleteResponse.Prediction.MatchedSubstring matched =
                                new PlacesAutocompleteResponse.Prediction.MatchedSubstring(
                                        matchNode.get("length").asInt(),
                                        matchNode.get("offset").asInt()
                                );
                        matchedSubstrings.add(matched);
                    }
                }
                prediction.setMatchedSubstrings(matchedSubstrings);

                predictions.add(prediction);
            }
        }

        return new PlacesAutocompleteResponse(status, predictions);
    }

    @Override
    public PlacesAutocompleteResponse getPlacesAutocomplete(String input, String language, String sessionToken) {
        try {
            String url = buildPlacesAutocompleteUrl(input, language, sessionToken);
            log.info("🔍 Places autocomplete for: {}", input);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(httpResponse.body());

            return processPlacesResponse(response);

        } catch (Exception e) {
            log.error("Error during places autocomplete", e);
            return new PlacesAutocompleteResponse("ERROR", new ArrayList<>());
        }
    }

    private String buildReverseGeocodeUrl(ReverseGeocodingRequest request) {
        String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("latlng", request.getLat() + "," + request.getLng())
                .queryParam("key", config.getApiKey())
                .queryParam("language", request.getLanguage())
                .queryParam("region", request.getRegion())
                .toUriString();
    }

    private String buildPlacesAutocompleteUrl(String input, String language, String sessionToken) {
        String baseUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("input", input)
                .queryParam("key", config.getApiKey())
                .queryParam("language", language)
                .queryParam("components", "country:id");

        if (sessionToken != null) {
            builder.queryParam("sessiontoken", sessionToken);
        }

        return builder.toUriString();
    }
}