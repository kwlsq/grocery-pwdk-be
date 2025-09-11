package com.pwdk.grocereach.location.presentations.dtos;

import com.pwdk.grocereach.location.domains.entities.City;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityResponse {
    private Integer id;
    private String name;

    public CityResponse(City city) {
        this.id = city.getId();
        this.name = city.getName();
    }
}
