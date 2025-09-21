package com.pwdk.grocereach.location.presentations.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RajaOngkirMeta {
    private String message;
    private Integer code;
    private String status;
}
