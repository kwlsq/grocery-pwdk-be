package com.pwdk.grocereach.location.applications.Implements;

import com.pwdk.grocereach.location.applications.services.LocationService;
import com.pwdk.grocereach.location.domains.entities.City;
import com.pwdk.grocereach.location.domains.entities.Province;
import com.pwdk.grocereach.location.infrastructures.repositories.CityRepository;
import com.pwdk.grocereach.location.infrastructures.repositories.ProvinceRepository;
import com.pwdk.grocereach.location.presentations.dtos.CityResponse;
import com.pwdk.grocereach.location.presentations.dtos.ProvinceResponse;
import com.pwdk.grocereach.location.presentations.dtos.RajaOngkirDTOs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final RestTemplate restTemplate;

    @Value("${rajaongkir.api.key}")
    private String rajaOngkirApiKey;

    @Value("${rajaongkir.api.url:https://rajaongkir.komerce.id/api/v1}")
    private String rajaOngkirApiUrl;

    @Override
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAll().stream()
                .map(ProvinceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CityResponse> getCitiesByProvince(Integer provinceId) {
        String url = rajaOngkirApiUrl + "/destination/city/" + provinceId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("key", rajaOngkirApiKey);
        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<RajaOngkirDTOs.ApiResponse<List<RajaOngkirDTOs.CityData>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, request,
                    new ParameterizedTypeReference<>() {}
            );

            RajaOngkirDTOs.ApiResponse<List<RajaOngkirDTOs.CityData>> apiResponse = response.getBody();

            if (apiResponse != null && "success".equalsIgnoreCase(apiResponse.getMeta().getStatus())) {
                Province province = provinceRepository.findById(provinceId)
                        .orElseThrow(() -> new RuntimeException("Province not found for ID: " + provinceId));

                List<City> cities = apiResponse.getData().stream().map(cityData -> {
                    return cityRepository.findByRajaOngkirId(cityData.getId()).orElseGet(() -> {
                        City newCity = new City();
                        newCity.setId(cityData.getId());
                        newCity.setName(cityData.getName());
                        newCity.setRajaOngkirId(cityData.getId());
                        newCity.setProvince(province);
                        return cityRepository.save(newCity);
                    });
                }).collect(Collectors.toList());

                return cities.stream().map(CityResponse::new).collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Error fetching from RajaOngkir or saving cities: " + e.getMessage());
        }

        return Collections.emptyList();
    }
}
