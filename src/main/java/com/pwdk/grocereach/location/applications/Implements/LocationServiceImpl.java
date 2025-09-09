package com.pwdk.grocereach.location.applications.Implements;

import com.pwdk.grocereach.location.applications.services.LocationService;
import com.pwdk.grocereach.location.infrastructures.repositories.CityRepository;
import com.pwdk.grocereach.location.infrastructures.repositories.ProvinceRepository;
import com.pwdk.grocereach.location.presentations.dtos.CityResponse;
import com.pwdk.grocereach.location.presentations.dtos.ProvinceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;

    @Override
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAll().stream()
                .map(ProvinceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CityResponse> getCitiesByProvince(Integer provinceId) {
        return cityRepository.findByProvinceId(provinceId).stream()
                .map(CityResponse::new)
                .collect(Collectors.toList());
    }
}
