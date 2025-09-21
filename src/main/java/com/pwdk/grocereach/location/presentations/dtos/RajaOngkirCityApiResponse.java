package com.pwdk.grocereach.location.presentations.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RajaOngkirCityApiResponse {
    private RajaOngkirMeta meta;
    private List<RajaOngkirCityData> data;
}

