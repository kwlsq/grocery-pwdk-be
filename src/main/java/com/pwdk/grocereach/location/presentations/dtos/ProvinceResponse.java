package com.pwdk.grocereach.location.presentations.dtos;

import com.pwdk.grocereach.location.domains.entities.Province;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProvinceResponse {
    private Integer id;
    private String name;

    public ProvinceResponse(Province province) {
        this.id = province.getId();
        this.name = province.getName();
    }
}
