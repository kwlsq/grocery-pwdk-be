package com.pwdk.grocereach.shipping.presentations.dto.rajaongkir;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KomerceMetaData {
    private String message;
    private Integer code;
    private String status;
}
