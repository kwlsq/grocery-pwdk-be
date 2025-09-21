package com.pwdk.grocereach.location.presentations.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RajaOngkirCityData {
    private Integer id;
    private String name;
    private String type; // e.g., "Kota", "Kabupaten"
}
