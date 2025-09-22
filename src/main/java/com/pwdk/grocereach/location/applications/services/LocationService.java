package com.pwdk.grocereach.location.applications.services;

import com.pwdk.grocereach.location.presentations.dtos.CityResponse;
import com.pwdk.grocereach.location.presentations.dtos.ProvinceResponse;

import java.util.List;

public interface LocationService {
    List<ProvinceResponse> getAllProvinces();
    List<CityResponse> getCitiesByProvince(Integer provinceId);
}
