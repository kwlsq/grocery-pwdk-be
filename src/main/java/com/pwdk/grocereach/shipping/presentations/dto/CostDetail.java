package com.pwdk.grocereach.shipping.presentations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public  class CostDetail {
    private Long value;
    private String etd;
    private String note;
}
